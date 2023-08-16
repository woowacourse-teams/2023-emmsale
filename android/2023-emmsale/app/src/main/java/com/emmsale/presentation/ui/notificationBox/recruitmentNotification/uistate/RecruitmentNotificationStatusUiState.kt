package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

import com.emmsale.data.notification.recruitment.RecruitmentStatus

enum class RecruitmentNotificationStatusUiState {
    ACCEPTED,
    REJECTED,
    PENDING,
    ;

    companion object {
        fun from(recruitmentStatus: RecruitmentStatus): RecruitmentNotificationStatusUiState =
            when (recruitmentStatus) {
                RecruitmentStatus.ACCEPTED -> ACCEPTED
                RecruitmentStatus.REJECTED -> REJECTED
                RecruitmentStatus.PENDING -> PENDING
            }
    }
}