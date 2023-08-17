package com.emmsale.presentation.ui.notificationBox.recruitmentNotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationBodyUiState
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationEvent
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationHeaderUiState
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationMemberUiState
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class RecruitmentNotificationViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val eventDetailRepository: EventDetailRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _notifications = NotNullMutableLiveData(RecruitmentNotificationUiState())
    val notifications: NotNullLiveData<RecruitmentNotificationUiState> = _notifications

    private val _recruitmentUiState = NotNullMutableLiveData(RecruitmentUiState())
    val recruitmentUiState: NotNullLiveData<RecruitmentUiState> = _recruitmentUiState

    private val _event = MutableLiveData<RecruitmentNotificationEvent?>(null)
    val event: LiveData<RecruitmentNotificationEvent?> = _event

    init {
        fetchRecruitmentNotifications()
    }

    private fun fetchRecruitmentNotifications() {
        viewModelScope.launch {
            _notifications.postValue(_notifications.value.copy(isLoading = true))
            val uid = tokenRepository.getToken()?.uid ?: return@launch

            when (val result = notificationRepository.getRecruitmentNotifications(uid)) {
                is ApiSuccess -> updateNotifications(result.data)
                is ApiException, is ApiError -> _notifications.postValue(
                    _notifications.value.copy(
                        isLoading = false,
                        isLoadingNotificationsFailed = true,
                    ),
                )
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
            when (val member = memberRepository.getMember(userId)) {
                is ApiSuccess -> RecruitmentNotificationMemberUiState(
                    name = member.data.name,
                    profileImageUrl = member.data.imageUrl,
                    openChatUrl = member.data.openProfileUrl,
                )

                is ApiException, is ApiError -> null
            }
        }

    private suspend fun getConferenceNameAsync(conferenceId: Long): Deferred<String?> =
        viewModelScope.async {
            when (val conference = eventDetailRepository.getEventDetail(conferenceId)) {
                is ApiSuccess -> conference.data.name
                is ApiException, is ApiError -> null
            }
        }

    fun toggleExpand(eventId: Long) {
        _notifications.value = notifications.value.toggleNotificationExpanded(eventId)
    }

    fun acceptRecruit(notificationId: Long) {
        viewModelScope.launch {
            _recruitmentUiState.value = recruitmentUiState.value.changeToLoadingState()

            when (
                notificationRepository.updateRecruitmentStatus(
                    notificationId,
                    RecruitmentStatus.ACCEPTED,
                )
            ) {
                is ApiSuccess -> {
                    _recruitmentUiState.value = recruitmentUiState.value.changeToAcceptedState()
                    _notifications.value = notifications.value.changeAcceptStateBy(notificationId)
                }

                is ApiException, is ApiError ->
                    _recruitmentUiState.value =
                        recruitmentUiState.value.changeToUpdatingRecruitmentStatusErrorState()
            }
        }
    }

    fun rejectRecruit(notificationId: Long) {
        viewModelScope.launch {
            _recruitmentUiState.value = recruitmentUiState.value.changeToLoadingState()

            when (
                notificationRepository.updateRecruitmentStatus(
                    notificationId,
                    RecruitmentStatus.REJECTED,
                )
            ) {
                is ApiSuccess -> {
                    _recruitmentUiState.value = recruitmentUiState.value.changeToRejectedState()
                    _notifications.value = notifications.value.changeRejectStateBy(notificationId)
                }

                is ApiException, is ApiError ->
                    _recruitmentUiState.value =
                        recruitmentUiState.value.changeToUpdatingRecruitmentStatusErrorState()
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
                is ApiError, is ApiException ->
                    _event.value =
                        RecruitmentNotificationEvent.REPORT_FAIL

                is ApiSuccess -> _event.value = RecruitmentNotificationEvent.REPORT_SUCCESS
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
        val factory = ViewModelFactory {
            RecruitmentNotificationViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                eventDetailRepository = KerdyApplication.repositoryContainer.eventDetailRepository,
                notificationRepository = KerdyApplication.repositoryContainer.notificationRepository,
            )
        }
    }
}
