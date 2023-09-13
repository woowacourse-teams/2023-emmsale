package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ConferenceApiModel
import com.emmsale.data.model.Conference
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.EventApplyStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun List<ConferenceApiModel>.toData(): List<Conference> = map { it.toData() }

fun ConferenceApiModel.toData(): Conference = Conference(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    status = status.toData(),
    tags = tags,
    posterUrl = posterUrl,
    dDay = dDay,
    eventApplyStatus = applyStatus.mapToApplyStatus(),
    applyRemainingDays = applyRemainingDays,
    isOnline = isOnline,
    isFree = isFree,
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}

private fun String.mapToApplyStatus(): EventApplyStatus = when (this) {
    "IN_PROGRESS" -> EventApplyStatus.IN_PROGRESS
    "UPCOMING" -> EventApplyStatus.UPCOMING
    "ENDED" -> EventApplyStatus.ENDED
    else -> throw IllegalArgumentException("알 수 없는 신청 상태입니다. api 스펙을 다시 확인해주세요.")
}

private fun String.toData(): ConferenceStatus = when (this) {
    "IN_PROGRESS" -> ConferenceStatus.IN_PROGRESS
    "UPCOMING" -> ConferenceStatus.SCHEDULED
    "ENDED" -> ConferenceStatus.ENDED
    else -> throw IllegalArgumentException("Unknown conference status: $this")
}
