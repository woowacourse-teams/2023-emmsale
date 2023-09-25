package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ConferenceResponse
import com.emmsale.data.model.Conference
import com.emmsale.data.model.EventStatus
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun List<ConferenceResponse>.toData(): List<Conference> = map { it.toData() }

fun ConferenceResponse.toData(): Conference = Conference(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    status = when (status) {
        ConferenceResponse.Status.ENDED -> EventStatus.ENDED
        ConferenceResponse.Status.UPCOMING -> EventStatus.UPCOMING
        ConferenceResponse.Status.IN_PROGRESS -> EventStatus.IN_PROGRESS
    },
    tags = tags,
    posterUrl = posterUrl,
    remainingDays = remainingDays,
    eventStatus = when (applyStatus) {
        ConferenceResponse.Status.ENDED -> EventStatus.ENDED
        ConferenceResponse.Status.UPCOMING -> EventStatus.UPCOMING
        ConferenceResponse.Status.IN_PROGRESS -> EventStatus.IN_PROGRESS
    },
    applyRemainingDays = applyRemainingDays,
    onOfflineMode = when (onOfflineMode) {
        ConferenceResponse.OnOfflineMode.ONLINE -> OnOfflineMode.ONLINE
        ConferenceResponse.OnOfflineMode.OFFLINE -> OnOfflineMode.OFFLINE
        ConferenceResponse.OnOfflineMode.ON_OFFLINE -> OnOfflineMode.ON_OFFLINE
    },
    paymentType = when (paymentType) {
        ConferenceResponse.PaymentType.FREE -> PaymentType.FREE
        ConferenceResponse.PaymentType.PAID -> PaymentType.PAID
        ConferenceResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
    },
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}
