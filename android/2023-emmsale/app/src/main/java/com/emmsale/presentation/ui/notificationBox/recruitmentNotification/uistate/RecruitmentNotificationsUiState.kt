package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationsUiState(
    val notificationGroups: List<RecruitmentNotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): RecruitmentNotificationsUiState =
        copy(notificationGroups = toggleExpanded(eventId))

    fun changeAcceptStateBy(notificationId: Long): RecruitmentNotificationsUiState =
        copy(notificationGroups = notificationGroups.map { it.changeToAcceptedStateBy(notificationId) })

    fun changeRejectStateBy(notificationId: Long): RecruitmentNotificationsUiState =
        copy(notificationGroups = notificationGroups.map { it.changeToRejectedStateBy(notificationId) })

    fun changeReadStateBy(eventId: Long): RecruitmentNotificationsUiState =
        copy(notificationGroups = notificationGroups.map {
            if (it.eventId == eventId) it.changeToReadState() else it
        })

    private fun toggleExpanded(eventId: Long): List<RecruitmentNotificationHeaderUiState> {
        return notificationGroups.map { header ->
            if (header.eventId == eventId) {
                header.toggleExpanded()
            } else {
                header
            }
        }
    }
}
