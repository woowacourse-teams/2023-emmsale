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
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationBodyUiState
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationHeaderUiState
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationMemberUiState
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
                    val notifications: List<NotificationBodyUiState> =
                        result.data.map { notification ->
                            val notiMember = getNotificationMemberAsync(notification.otherUid)
                            val conferenceName = getConferenceNameAsync(notification.eventId)
                            awaitAll(notiMember, conferenceName).run {
                                createNotificationUiState(
                                    notification = notification,
                                    notificationMember = getOrNull(0) as? NotificationMemberUiState,
                                    conferenceName = getOrNull(1).toString(),
                                )
                            }
                        }

                    val finalNotification = notifications.groupBy { it.conferenceId }
                    val notificationHeaders =
                        finalNotification.map { (conferenceId, notifications) ->
                            NotificationHeaderUiState(
                                eventId = conferenceId,
                                conferenceName = notifications[0].conferenceName,
                                notifications = notifications,
                            )
                        }

                    _notifications.postValue(NotificationsUiState(notifications = notificationHeaders))
                }

                is ApiException,
                is ApiError,
                -> _notifications.postValue(NotificationsUiState(isError = false))
            }
        }
    }

    private fun createNotificationUiState(
        notification: Notification,
        notificationMember: NotificationMemberUiState?,
        conferenceName: String,
    ): NotificationBodyUiState = NotificationBodyUiState(
        id = notification.id,
        otherUid = notification.otherUid,
        otherName = notificationMember?.name ?: "",
        conferenceId = notification.eventId,
        conferenceName = conferenceName,
        message = notification.message,
        profileImageUrl = notificationMember?.profileImageUrl ?: "",
    )

    private suspend fun getNotificationMemberAsync(userId: Long): Deferred<NotificationMemberUiState?> =
        viewModelScope.async {
            when (val member = memberRepository.getMember(userId)) {
                is ApiSuccess -> NotificationMemberUiState(member.data.name, member.data.imageUrl)
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
    // TODO: 컨퍼런스 동행 수락
    fun acceptCompanion(notificationId: Long) {
        Log.d("kerdy", notificationId.toString())
    }

    // TODO: 4차 스프린트 구현 예정
    // TODO: 컨퍼런스 동행 거절
    fun rejectCompanion(notificationId: Long) {
        Log.d("kerdy", notificationId.toString())
    }

    fun toggleExpand(eventId: Long) {
        _notifications.postValue(
            _notifications.value?.copy(
                notifications = _notifications.value?.notifications?.map { header ->
                    when (header.eventId == eventId) {
                        true -> header.copy(isExpanded = !header.isExpanded)
                        false -> header
                    }
                } ?: emptyList()
            )
        )
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
