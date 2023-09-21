package com.emmsale.presentation.ui.primaryNotificationList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationScreenUiState
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationsUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrimaryNotificationViewModel @Inject constructor(
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
                is Failure, NetworkError ->
                    _uiState.value = PrimaryNotificationScreenUiState.Error

                is Success ->
                    _uiState.value = PrimaryNotificationScreenUiState.Success.from(result.data)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState.Success) return@launch
            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (
                val result =
                    notificationRepository.deleteUpdatedNotifications(pastNotificationIds)
            ) {
                is Failure, NetworkError ->
                    _uiEvent.value = Event(PrimaryNotificationsUiEvent.DELETE_FAIL)

                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun readNotification(notificationId: Long) {
        viewModelScope.launch {
            when (
                val result =
                    notificationRepository.updateUpdatedNotificationReadStatus(notificationId)
            ) {
                is Failure, NetworkError -> Unit
                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            when (
                val result =
                    notificationRepository.deleteUpdatedNotifications(listOf(notificationId))
            ) {
                is Failure, NetworkError ->
                    _uiEvent.value = Event(PrimaryNotificationsUiEvent.DELETE_FAIL)

                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
