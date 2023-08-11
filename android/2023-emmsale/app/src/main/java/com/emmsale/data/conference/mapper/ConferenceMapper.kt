package com.emmsale.data.conference.mapper

import com.emmsale.data.conference.Conference
import com.emmsale.data.conference.dto.ConferenceApiModel
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
    applyStatus = applyStatus.mapToApplyStatus(),
    applyRemainingDays = applyRemainingDays,
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}
