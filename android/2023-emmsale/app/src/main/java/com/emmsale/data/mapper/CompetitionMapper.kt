package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.model.Competition
import com.emmsale.data.model.EventMode
import com.emmsale.data.model.PaymentType
import com.emmsale.data.model.Status
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun List<CompetitionResponse>.toData(): List<Competition> = map { it.toData() }

fun CompetitionResponse.toData(): Competition = Competition(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    status = when (status) {
        CompetitionResponse.Status.ENDED -> Status.ENDED
        CompetitionResponse.Status.UPCOMING -> Status.UPCOMING
        CompetitionResponse.Status.IN_PROGRESS -> Status.IN_PROGRESS
    },
    tags = tags,
    posterUrl = posterUrl,
    dDay = dDay,
    eventStatus = when (applyStatus) {
        CompetitionResponse.Status.ENDED -> Status.ENDED
        CompetitionResponse.Status.UPCOMING -> Status.UPCOMING
        CompetitionResponse.Status.IN_PROGRESS -> Status.IN_PROGRESS
    },
    applyRemainingDays = applyRemainingDays,
    eventMode = when (eventMode) {
        CompetitionResponse.EventMode.ONLINE -> EventMode.ONLINE
        CompetitionResponse.EventMode.OFFLINE -> EventMode.OFFLINE
        CompetitionResponse.EventMode.ON_OFFLINE -> EventMode.ON_OFFLINE
    },
    paymentType = when (paymentType) {
        CompetitionResponse.PaymentType.FREE -> PaymentType.FREE
        CompetitionResponse.PaymentType.PAID -> PaymentType.PAID
        CompetitionResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
    },
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}
