package com.emmsale.data.conference.mapper

import com.emmsale.data.eventApplyStatus.EventApplyStatus

fun String.mapToApplyStatus(): EventApplyStatus = when (this) {
    "IN_PROGRESS" -> EventApplyStatus.IN_PROGRESS
    "UPCOMING" -> EventApplyStatus.UPCOMING
    "ENDED" -> EventApplyStatus.ENDED
    else -> throw IllegalArgumentException("알 수 없는 신청 상태입니다. api 스펙을 다시 확인해주세요.")
}
