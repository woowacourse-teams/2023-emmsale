package com.emmsale.presentation.ui.notificationBox.primaryNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uievent.PrimaryNotificationsUiEvent
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel(), Refreshable {

    private val _uiState =
        NotNullMutableLiveData<PrimaryNotificationScreenUiState>(PrimaryNotificationScreenUiState.Loading)
    val uiState: NotNullLiveData<PrimaryNotificationScreenUiState> = _uiState

    private val _uiEvent = NotNullMutableLiveData(Event(PrimaryNotificationsUiEvent.NONE))
    val uiEvent: NotNullLiveData<Event<PrimaryNotificationsUiEvent>> = _uiEvent

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            val uid = tokenRepository.getToken()?.uid ?: return@launch
            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is ApiError, is ApiException ->
                    _uiState.value =
                        PrimaryNotificationScreenUiState.Error

                is ApiSuccess ->
                    _uiState.value =
                        PrimaryNotificationScreenUiState.Success.from(result.data)
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState.Success) return@launch
            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (notificationRepository.deleteUpdatedNotifications(pastNotificationIds)) {
                is ApiSuccess -> refresh()
                is ApiError, is ApiException ->
                    _uiEvent.value =
                        Event(PrimaryNotificationsUiEvent.DELETE_FAIL)
            }
        }
    }

    fun readNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.updateUpdatedNotificationReadStatus(notificationId)) {
                is ApiSuccess -> refresh()
                is ApiError, is ApiException -> Unit
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.deleteUpdatedNotifications(listOf(notificationId))) {
                is ApiSuccess -> refresh()
                is ApiError, is ApiException ->
                    _uiEvent.value =
                        Event(PrimaryNotificationsUiEvent.DELETE_FAIL)
            }
        }
    }

    companion object {
        val factory: ViewModelFactory<PrimaryNotificationViewModel> = ViewModelFactory {
            PrimaryNotificationViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
            )
        }
    }
}
