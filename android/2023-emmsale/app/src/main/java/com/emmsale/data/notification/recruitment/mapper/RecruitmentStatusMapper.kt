package com.emmsale.data.notification.recruitment.mapper

import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.notification.recruitment.dto.RecruitmentStatusUpdateRequestModel

private const val IN_PROGRESS = "IN_PROGRESS"
private const val ACCEPT = "ACCEPTED"
private const val REJECT = "REJECTED"
private const val INVALID_STATUS_ERROR_MESSAGE = "올바르지 않은 상태입니다."

fun String.toRecruitmentStatus(): RecruitmentStatus =
    when (this) {
        IN_PROGRESS -> RecruitmentStatus.PENDING
        ACCEPT -> RecruitmentStatus.ACCEPTED
        REJECT -> RecruitmentStatus.REJECTED
        else -> throw IllegalArgumentException(INVALID_STATUS_ERROR_MESSAGE)
    }

fun RecruitmentStatus.toRequestModel(): RecruitmentStatusUpdateRequestModel =
    when (this) {
        RecruitmentStatus.PENDING -> RecruitmentStatusUpdateRequestModel(IN_PROGRESS)
        RecruitmentStatus.ACCEPTED -> RecruitmentStatusUpdateRequestModel(ACCEPT)
        RecruitmentStatus.REJECTED -> RecruitmentStatusUpdateRequestModel(REJECT)
    }
