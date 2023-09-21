package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ConferenceResponse
import com.emmsale.data.model.Conference
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
        ConferenceResponse.Status.ENDED -> Conference.Status.ENDED
        ConferenceResponse.Status.UPCOMING -> Conference.Status.UPCOMING
        ConferenceResponse.Status.IN_PROGRESS -> Conference.Status.IN_PROGRESS
    },
    tags = tags,
    posterUrl = posterUrl,
    dDay = dDay,
    eventStatus = when (applyStatus) {
        ConferenceResponse.Status.ENDED -> Conference.Status.ENDED
        ConferenceResponse.Status.UPCOMING -> Conference.Status.UPCOMING
        ConferenceResponse.Status.IN_PROGRESS -> Conference.Status.IN_PROGRESS
    },
    applyRemainingDays = applyRemainingDays,
    eventMode = when (eventMode) {
        ConferenceResponse.EventMode.ONLINE -> Conference.EventMode.ONLINE
        ConferenceResponse.EventMode.OFFLINE -> Conference.EventMode.OFFLINE
        ConferenceResponse.EventMode.ON_OFFLINE -> Conference.EventMode.ON_OFFLINE
    },
    paymentType = when (paymentType) {
        ConferenceResponse.PaymentType.FREE -> Conference.PaymentType.FREE
        ConferenceResponse.PaymentType.PAID -> Conference.PaymentType.PAID
        ConferenceResponse.PaymentType.PAID_OR_FREE -> Conference.PaymentType.PAID_OR_FREE
    },
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}
