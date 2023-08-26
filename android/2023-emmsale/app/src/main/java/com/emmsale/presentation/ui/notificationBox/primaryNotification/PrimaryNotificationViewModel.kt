package com.emmsale.presentation.ui.notificationBox.primaryNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.RefreshableViewModel
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiEvent
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel(), RefreshableViewModel {

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
                    _uiState.value = PrimaryNotificationScreenUiState.Error

                is ApiSuccess ->
                    _uiState.value = PrimaryNotificationScreenUiState.Success.from(result.data)
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState.Success) return@launch
            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (notificationRepository.deleteUpdatedNotifications(pastNotificationIds)) {
                is ApiError, is ApiException ->
                    _uiEvent.value = Event(PrimaryNotificationsUiEvent.DELETE_FAIL)

                is ApiSuccess -> refresh()
            }
        }
    }

    fun readNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.updateUpdatedNotificationReadStatus(notificationId)) {
                is ApiError, is ApiException -> {}

                is ApiSuccess -> refresh()
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.deleteUpdatedNotifications(listOf(notificationId))) {
                is ApiError, is ApiException ->
                    _uiEvent.value = Event(PrimaryNotificationsUiEvent.DELETE_FAIL)

                is ApiSuccess -> refresh()
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
