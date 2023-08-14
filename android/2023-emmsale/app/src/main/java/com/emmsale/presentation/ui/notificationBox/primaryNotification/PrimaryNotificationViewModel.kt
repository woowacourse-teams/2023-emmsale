package com.emmsale.presentation.ui.notificationBox.primaryNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.notification.updated.UpdatedNotification
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiState
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _pastNotifications = NotNullMutableLiveData(PrimaryNotificationsUiState())
    val pastNotifications: NotNullLiveData<PrimaryNotificationsUiState> = _pastNotifications

    private val _recentNotifications = NotNullMutableLiveData(PrimaryNotificationsUiState())
    val recentNotifications: NotNullLiveData<PrimaryNotificationsUiState> = _recentNotifications

    init {
        fetchPrimaryNotifications()
    }

    private fun fetchPrimaryNotifications() {
        viewModelScope.launch {
            _recentNotifications.value = recentNotifications.value.copy(isLoading = true)

            val uid = tokenRepository.getToken()?.uid ?: return@launch
            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is ApiSuccess -> {
                    updatePastNotifications(result.data.filter { it.isPast })
                    updateNewNotifications(result.data.filterNot { it.isPast })
                }

                is ApiError, is ApiException ->
                    _recentNotifications.value =
                        recentNotifications.value.copy(isFetchingError = true)
            }
        }
    }

    private fun updateNewNotifications(notifications: List<UpdatedNotification>) {
        _recentNotifications.value = PrimaryNotificationsUiState.from(notifications)
    }

    private fun updatePastNotifications(notifications: List<UpdatedNotification>) {
        _pastNotifications.value = PrimaryNotificationsUiState.from(notifications)
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
