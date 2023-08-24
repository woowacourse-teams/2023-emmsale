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
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState1
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiEvent
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel1(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel(), RefreshableViewModel {

    private val _uiState =
        NotNullMutableLiveData<PrimaryNotificationScreenUiState1>(PrimaryNotificationScreenUiState1.Loading)
    val uiState: NotNullLiveData<PrimaryNotificationScreenUiState1> = _uiState

    private val _uiEvent = MutableLiveData<PrimaryNotificationsUiEvent?>(null)
    val uiEvent: LiveData<PrimaryNotificationsUiEvent?> = _uiEvent

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            val uid = tokenRepository.getToken()?.uid ?: return@launch
            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is ApiError, is ApiException ->
                    _uiState.value = PrimaryNotificationScreenUiState1.Error

                is ApiSuccess -> {
                    val recentNotifications = result.data.filterNot { it.isRead }
                    val pastNotifications = result.data.filter { it.isRead }
                    _uiState.value = PrimaryNotificationScreenUiState1.Success.create(
                        recentNotifications,
                        pastNotifications,
                    )
                }
            }
        }
    }

    fun deleteAllPastNotifications() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState !is PrimaryNotificationScreenUiState1.Success) return@launch
            val pastNotificationIds = currentUiState.pastNotifications.map { it.notificationId }
            when (notificationRepository.deleteUpdatedNotifications(pastNotificationIds)) {
                is ApiError, is ApiException ->
                    _uiEvent.value = PrimaryNotificationsUiEvent.DELETE_ERROR

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
                    _uiEvent.value = PrimaryNotificationsUiEvent.DELETE_ERROR

                is ApiSuccess -> refresh()
            }
        }
    }

    fun resetUiEvent() {
        _uiEvent.value = null
    }

    companion object {
        val factory: ViewModelFactory<PrimaryNotificationViewModel1> = ViewModelFactory {
            PrimaryNotificationViewModel1(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
            )
        }
    }
}
