package com.emmsale.data.mapper

import com.emmsale.data.apiModel.request.RecruitmentNotificationStatusUpdateRequest
import com.emmsale.data.model.RecruitmentStatus

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

fun RecruitmentStatus.toRequestModel(): RecruitmentNotificationStatusUpdateRequest =
    when (this) {
        RecruitmentStatus.PENDING -> RecruitmentNotificationStatusUpdateRequest(IN_PROGRESS)
        RecruitmentStatus.ACCEPTED -> RecruitmentNotificationStatusUpdateRequest(ACCEPT)
        RecruitmentStatus.REJECTED -> RecruitmentNotificationStatusUpdateRequest(REJECT)
    }
