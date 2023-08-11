package com.emmsale.data.conference.mapper

import com.emmsale.data.applyStatus.ApplyStatus

fun String.mapToApplyStatus(): ApplyStatus = when (this) {
    "IN_PROGRESS" -> ApplyStatus.IN_PROGRESS
    "UPCOMING" -> ApplyStatus.UPCOMING
    "ENDED" -> ApplyStatus.ENDED
    else -> throw IllegalArgumentException("알 수 없는 신청 상태입니다. api 스펙을 다시 확인해주세요.")
}
