package com.emmsale.presentation.ui.notificationBox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.notification.Notification
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationUiState
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationsUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NotificationBoxViewModel(
    private val memberRepository: MemberRepository,
    private val eventDetailRepository: EventDetailRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _notifications = MutableLiveData<NotificationsUiState>()
    val notifications: LiveData<NotificationsUiState> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            _notifications.postValue(NotificationsUiState(isLoading = true))

            when (val result = notificationRepository.getNotifications()) {
                is ApiSuccess -> {
                    val notifications: List<NotificationUiState> = result.data.map { notification ->
                        val otherName = getMemberNameAsync(notification.otherUid)
                        val conferenceName = getConferenceNameAsync(notification.eventId)
                        awaitAll(otherName, conferenceName).run {
                            createNotificationUiState(
                                notification = notification,
                                otherName = getOrNull(0).toString(),
                                conferenceName = getOrNull(0).toString(),
                            )
                        }
                    }
                    _notifications.postValue(NotificationsUiState(notifications = notifications))
                }

                is ApiException,
                is ApiError,
                -> _notifications.postValue(NotificationsUiState(isError = false))
            }
        }
    }

    private fun createNotificationUiState(
        notification: Notification,
        otherName: String,
        conferenceName: String,
    ) = NotificationUiState(
        id = notification.id,
        otherUid = notification.otherUid,
        otherName = otherName,
        conferenceId = notification.eventId,
        conferenceName = conferenceName,
    )

    private suspend fun getMemberNameAsync(userId: Long): Deferred<String?> =
        viewModelScope.async {
            when (val member = memberRepository.getMember(userId)) {
                is ApiSuccess -> member.data.name
                is ApiException,
                is ApiError,
                -> null
            }
        }

    private suspend fun getConferenceNameAsync(conferenceId: Long): Deferred<String?> =
        viewModelScope.async {
            when (val conference = eventDetailRepository.fetchEventDetail(conferenceId)) {
                is ApiSuccess -> conference.data.name
                is ApiException,
                is ApiError,
                -> null
            }
        }

    // TODO: 4차 스프린트 구현 예정
    fun deleteNotification(notificationId: Long) {
        Log.d("kerdy", notificationId.toString())
    }

    companion object {
        val factory = ViewModelFactory {
            NotificationBoxViewModel(
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                eventDetailRepository = KerdyApplication.repositoryContainer.eventDetailRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
            )
        }
    }
}
