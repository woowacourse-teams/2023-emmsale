package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationUiState(
    val notificationGroups: List<RecruitmentNotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingNotificationsFailed: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): RecruitmentNotificationUiState =
        copy(notificationGroups = toggleExpanded(eventId))

    fun changeAcceptStateBy(notificationId: Long): RecruitmentNotificationUiState =
        copy(notificationGroups = notificationGroups.map { it.changeToAcceptedStateBy(notificationId) })

    fun changeRejectStateBy(notificationId: Long): RecruitmentNotificationUiState =
        copy(notificationGroups = notificationGroups.map { it.changeToRejectedStateBy(notificationId) })

    fun changeReadStateBy(eventId: Long): RecruitmentNotificationUiState =
        copy(
            notificationGroups = notificationGroups.map {
                if (it.eventId == eventId) it.changeToReadState() else it
            },
        )

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
