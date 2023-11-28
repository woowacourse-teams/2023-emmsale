package com.emmsale.presentation.ui.primaryNotificationList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.UiEvent
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

    private val _allNotificationsUiState =
        NotNullMutableLiveData<PrimaryNotificationScreenUiState>(PrimaryNotificationScreenUiState.Loading)
    val uiState: NotNullLiveData<PrimaryNotificationScreenUiState> = _allNotificationsUiState

    private val _uiEvent = NotNullMutableLiveData(UiEvent(PrimaryNotificationsUiEvent.NONE))
    val uiEvent: NotNullLiveData<UiEvent<PrimaryNotificationsUiEvent>> = _uiEvent

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            val uid = tokenRepository.getToken()?.uid ?: return@launch

            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is Failure, NetworkError ->
                    _allNotificationsUiState.value = PrimaryNotificationScreenUiState.Error

                is Success ->
                    _allNotificationsUiState.value =
                        PrimaryNotificationScreenUiState.Success.from(result.data)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = _allNotificationsUiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState.Success) return@launch
            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (
                val result =
                    notificationRepository.deleteUpdatedNotifications(pastNotificationIds)
            ) {
                is Failure, NetworkError ->
                    _uiEvent.value = UiEvent(PrimaryNotificationsUiEvent.DELETE_FAIL)

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
                    _uiEvent.value = UiEvent(PrimaryNotificationsUiEvent.DELETE_FAIL)

                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
