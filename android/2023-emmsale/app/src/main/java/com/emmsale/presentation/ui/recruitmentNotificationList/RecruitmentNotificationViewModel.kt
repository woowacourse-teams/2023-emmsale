package com.emmsale.presentation.ui.recruitmentNotificationList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.RecruitmentNotification
import com.emmsale.data.model.RecruitmentStatus
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationBodyUiState
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationHeaderUiState
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationMemberUiState
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationUiEvent
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class RecruitmentNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val eventRepository: EventRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel(), Refreshable {
    private val _notifications = NotNullMutableLiveData(RecruitmentNotificationUiState())
    val notifications: NotNullLiveData<RecruitmentNotificationUiState> = _notifications

    private val _event = MutableLiveData<RecruitmentNotificationUiEvent?>(null)
    val event: LiveData<RecruitmentNotificationUiEvent?> = _event

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            _notifications.value = _notifications.value.copy(isLoading = true)
            val uid = tokenRepository.getToken()?.uid ?: return@launch

            when (val result = notificationRepository.getRecruitmentNotifications(uid)) {
                is Failure, NetworkError -> _notifications.value = _notifications.value.copy(
                    isLoading = false,
                    isError = true,
                )

                is Success -> updateNotifications(result.data)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private suspend fun updateNotifications(
        recruitmentNotifications: List<RecruitmentNotification>,
    ) {
        val notifications =
            convertToNotificationBodies(recruitmentNotifications).groupBy { it.eventId }
        val notificationHeaders = notifications.map { (conferenceId, notifications) ->
            RecruitmentNotificationHeaderUiState(
                eventId = conferenceId,
                conferenceName = notifications.first().eventName,
                notifications = notifications,
            )
        }

        _notifications.value = _notifications.value.copy(
            isLoading = false,
            isError = false,
            notificationGroups = notificationHeaders,
        )
    }

    private suspend fun convertToNotificationBodies(
        recruitmentNotifications: List<RecruitmentNotification>,
    ): List<RecruitmentNotificationBodyUiState> = recruitmentNotifications.map { notification ->
        val notiMemberDeferred = getNotificationMemberAsync(notification.senderUid)
        val conferenceNameDeferred = getConferenceNameAsync(notification.eventId)
        val (notiMember, conferenceName) = awaitAll(notiMemberDeferred, conferenceNameDeferred)

        RecruitmentNotificationBodyUiState.from(
            recruitmentNotification = notification,
            notificationMember = notiMember as? RecruitmentNotificationMemberUiState,
            conferenceName = conferenceName.toString(),
        )
    }

    private suspend fun getNotificationMemberAsync(userId: Long): Deferred<RecruitmentNotificationMemberUiState?> =
        viewModelScope.async {
            when (val result = memberRepository.getMember(userId)) {
                is Failure, NetworkError -> null
                is Success -> RecruitmentNotificationMemberUiState(
                    name = result.data.name,
                    profileImageUrl = result.data.imageUrl,
                    openChatUrl = result.data.openProfileUrl,
                )

                is Unexpected -> throw Throwable(result.error)
            }
        }

    private suspend fun getConferenceNameAsync(conferenceId: Long): Deferred<String?> =
        viewModelScope.async {
            when (val conference = eventRepository.getEventDetail(conferenceId)) {
                is Success -> conference.data.name
                is Failure, NetworkError, is Unexpected -> null
            }
        }

    fun toggleExpand(eventId: Long) {
        _notifications.value = notifications.value.toggleNotificationExpanded(eventId)
    }

    fun acceptRecruit(notificationId: Long) {
        viewModelScope.launch {
            val result = notificationRepository.updateRecruitmentStatus(
                notificationId,
                RecruitmentStatus.ACCEPTED,
            )
            when (result) {
                is Failure, NetworkError ->
                    _event.value = RecruitmentNotificationUiEvent.ACCEPT_FAIL

                is Success -> {
                    _event.value = RecruitmentNotificationUiEvent.ACCEPT_COMPLETE
                    _notifications.value = notifications.value.changeAcceptStateBy(notificationId)
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun rejectRecruit(notificationId: Long) {
        viewModelScope.launch {
            val result = notificationRepository.updateRecruitmentStatus(
                notificationId,
                RecruitmentStatus.REJECTED,
            )
            when (result) {
                is Failure, NetworkError ->
                    _event.value = RecruitmentNotificationUiEvent.REJECT_FAIL

                is Success -> {
                    _event.value = RecruitmentNotificationUiEvent.REJECT_COMPLETE
                    _notifications.value = notifications.value.changeRejectStateBy(notificationId)
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun reportRecruitmentNotification(notificationId: Long) {
        viewModelScope.launch {
            val uid = tokenRepository.getToken()?.uid ?: return@launch
            val recruitmentNotification =
                _notifications.value.notificationGroups.flatMap { header -> header.notifications }
                    .find { it.id == notificationId } ?: return@launch

            val result = notificationRepository.reportRecruitmentNotification(
                recruitmentNotificationId = recruitmentNotification.id,
                senderId = recruitmentNotification.senderUid,
                reporterId = uid,
            )
            when (result) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _event.value = RecruitmentNotificationUiEvent.REPORT_DUPLICATE
                    } else {
                        _event.value = RecruitmentNotificationUiEvent.REPORT_FAIL
                    }
                }

                NetworkError ->
                    _event.value = RecruitmentNotificationUiEvent.REPORT_FAIL

                is Success -> _event.value = RecruitmentNotificationUiEvent.REPORT_SUCCESS
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    fun updateNotificationsToReadStatusBy(eventId: Long) {
        viewModelScope.launch {
            notifications.value.notificationGroups.flatMap { group -> group.notifications }
                .forEach { notification -> updateNotificationReadStatus(notification) }
            _notifications.value = notifications.value.changeReadStateBy(eventId)
        }
    }

    private suspend fun updateNotificationReadStatus(
        notification: RecruitmentNotificationBodyUiState,
    ) {
        if (!notification.isRead) {
            notificationRepository.updateNotificationReadStatus(notification.id)
        }
    }

    companion object {
        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        val factory = ViewModelFactory {
            RecruitmentNotificationViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                eventRepository = KerdyApplication.repositoryContainer.eventRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
            )
        }
    }
}