package com.emmsale.presentation.ui.notificationList

import androidx.lifecycle.LiveData
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.notificationList.uiState.NotificationsUiEvent
import com.emmsale.presentation.ui.notificationList.uiState.NotificationsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val notificationRepository: NotificationRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _notifications = NotNullMutableLiveData(NotificationsUiState())
    val notifications: NotNullLiveData<NotificationsUiState> = _notifications

    private val _uiEvent = SingleLiveEvent<NotificationsUiEvent>()
    val uiEvent: LiveData<NotificationsUiEvent> = _uiEvent

    init {
        fetchNotifications()
    }

    private fun fetchNotifications(): Job = fetchData(
        fetchData = { notificationRepository.getNotifications(uid) },
        onSuccess = { _notifications.value = NotificationsUiState(it) },
    )

    override fun refresh(): Job = refreshData(
        refresh = { notificationRepository.getNotifications(uid) },
        onSuccess = { _notifications.value = NotificationsUiState(it) },
    )

    fun deleteAllPastNotifications(): Job = command(
        command = {
            val pastNotificationIds =
                _notifications.value.pastNotifications.map { it.id }
            notificationRepository.deleteNotifications(pastNotificationIds)
        },
        onSuccess = { _notifications.value = _notifications.value.deleteAllPastNotifications() },
        onFailure = { _, _ -> _uiEvent.value = NotificationsUiEvent.DeleteFail },
    )

    fun readNotification(notificationId: Long): Job = command(
        command = { notificationRepository.readNotification(notificationId) },
        onSuccess = {
            _notifications.value = _notifications.value.readNotification(notificationId)
        },
    )

    fun deleteNotification(notificationId: Long): Job = command(
        command = { notificationRepository.deleteNotifications(listOf(notificationId)) },
        onSuccess = {
            _notifications.value = _notifications.value.deleteNotification(notificationId)
        },
        onFailure = { _, _ -> _uiEvent.value = NotificationsUiEvent.DeleteFail },
    )
}
