package com.emmsale.presentation.ui.notificationBox.primaryNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.comment.CommentRepository
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
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiState
import kotlinx.coroutines.launch

class PrimaryNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
    private val commentRepository: CommentRepository,
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
            val uid = tokenRepository.getToken()?.uid ?: return@launch
            _recentNotifications.value = recentNotifications.value.copy(isLoading = true)

            when (val result = notificationRepository.getUpdatedNotifications(uid)) {
                is ApiSuccess -> {
                    updatePastNotifications(result.data.filter { it.isRead })
                    updateRecentNotifications(result.data.filterNot { it.isRead })
                }

                is ApiError, is ApiException -> {
                    _recentNotifications.value =
                        recentNotifications.value.copy(isLoading = false, isFetchingError = true)
                }
            }
        }
    }

    private fun updateRecentNotifications(notifications: List<UpdatedNotification>) {
        _recentNotifications.value = PrimaryNotificationsUiState(
            notifications = notifications.map(PrimaryNotificationUiState::from),
        )
    }

    private fun updatePastNotifications(notifications: List<UpdatedNotification>) {
        _pastNotifications.value = PrimaryNotificationsUiState(
            notifications = notifications.map(PrimaryNotificationUiState::from),
        )
    }

    fun changeToRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.updateUpdatedNotificationReadStatus(notificationId)
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            when (notificationRepository.deleteUpdatedNotifications(listOf(notificationId))) {
                is ApiSuccess ->
                    _pastNotifications.value =
                        pastNotifications.value.deleteNotificationById(notificationId)

                is ApiError, is ApiException ->
                    _pastNotifications.value =
                        pastNotifications.value.copy(isDeleteNotificationError = true)
            }
        }
    }

    fun deleteAllPastNotifications() {
        val pastNotificationIds = pastNotifications.value.notificationIds
        viewModelScope.launch {
            when (notificationRepository.deleteUpdatedNotifications(pastNotificationIds)) {
                is ApiSuccess -> _pastNotifications.value = PrimaryNotificationsUiState.EMPTY
                is ApiError, is ApiException ->
                    _pastNotifications.value =
                        pastNotifications.value.copy(isDeleteNotificationError = true)
            }
        }
    }

    companion object {
        private const val INVALID_COMMENT_ID_ERROR_MESSAGE = "댓글 정보를 가져오는데 실패했습니다."

        val factory: ViewModelFactory<PrimaryNotificationViewModel> = ViewModelFactory {
            PrimaryNotificationViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
