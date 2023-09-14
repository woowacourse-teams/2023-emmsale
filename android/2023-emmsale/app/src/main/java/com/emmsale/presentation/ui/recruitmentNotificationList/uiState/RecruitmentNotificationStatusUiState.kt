package com.emmsale.presentation.ui.recruitmentNotificationList.uiState

import com.emmsale.data.model.RecruitmentStatus

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
