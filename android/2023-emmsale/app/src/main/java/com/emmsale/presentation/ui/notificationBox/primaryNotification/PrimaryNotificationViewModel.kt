package com.emmsale.presentation.ui.notificationBox.primaryNotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.RefreshableViewModel
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uievent.PrimaryNotificationsUiEvent
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel(), RefreshableViewModel {

    private val _uiState =
        NotNullMutableLiveData<PrimaryNotificationScreenUiState>(PrimaryNotificationScreenUiState.Loading)
    val uiState: NotNullLiveData<PrimaryNotificationScreenUiState> = _uiState

    private val _uiEvent = MutableLiveData<PrimaryNotificationsUiEvent?>(null)
    val uiEvent: LiveData<PrimaryNotificationsUiEvent?> = _uiEvent

    init {
        refreshNotifications()
    }

    override fun refreshNotifications() {
        viewModelScope.launch {
            val uid = tokenRepository.getToken()?.uid ?: return@launch
            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is ApiError, is ApiException ->
                    _uiState.value = PrimaryNotificationScreenUiState.Error

                is ApiSuccess -> {
                    val recentNotifications = result.data.filterNot { it.isRead }
                    val pastNotifications = result.data.filter { it.isRead }
                    _uiState.value = PrimaryNotificationScreenUiState.Success.create(
                        recentNotifications,
                        pastNotifications,
                    )
                }
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = uiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState.Success) return@launch

            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (notificationRepository.deleteUpdatedNotifications(pastNotificationIds)) {
                is ApiSuccess -> refreshNotifications()
                is ApiError, is ApiException ->
                    _uiEvent.value = PrimaryNotificationsUiEvent.DELETE_ERROR
            }
        }
    }

    fun readNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.updateUpdatedNotificationReadStatus(notificationId)) {
                is ApiSuccess -> refreshNotifications()
                is ApiError, is ApiException -> Unit
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.deleteUpdatedNotifications(listOf(notificationId))) {
                is ApiSuccess -> refreshNotifications()
                is ApiError, is ApiException ->
                    _uiEvent.value = PrimaryNotificationsUiEvent.DELETE_ERROR
            }
        }
    }

    fun resetUiEvent() {
        _uiEvent.value = null
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
