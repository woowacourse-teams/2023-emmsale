package com.emmsale.data.eventdetail.mapper

import com.emmsale.data.eventdetail.EventDetail
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun EventDetailApiModel.toData(): EventDetail = EventDetail(
    id = id,
    name = name,
    informationUrl = informationUrl,
    startDate = startDate.toLocalDateTime(),
    endDate = endDate.toLocalDateTime(),
    applyStartDate = applyStartDate.toLocalDateTime(),
    applyEndDate = applyEndDate.toLocalDateTime(),
    location = location,
    status = status.toKoreanStatus(),
    applyStatus = applyStatus.toKoreanApplyStatus(),
    tags = tags,
    posterImageUrl = imageUrl,
    remainingDays = remainingDays,
    applyRemainingDays = applyRemainingDays,
    type = type,
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}

private fun String.toKoreanStatus(): String = when (this) {
    "IN_PROGRESS" -> "진행 중"
    "UPCOMING" -> "진행 예정"
    "ENDED" -> "마감"
    else -> throw IllegalArgumentException("행사 상세 정보의 상태를 변환하는 데 실패했습니다. api 스펙을 다시 확인해주세요.")
}

private fun String.toKoreanApplyStatus(): String = when (this) {
    "IN_PROGRESS" -> "진행 중"
    "UPCOMING" -> "진행 예정"
    "ENDED" -> "마감"
    else -> throw IllegalArgumentException("행사 상세 정보의 신청 상태를 변환하는 데 실패했습니다. api 스펙을 다시 확인해주세요.")
}
