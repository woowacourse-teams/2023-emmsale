package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.model.ScrappedEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun ScrappedEventResponse.toData(): ScrappedEvent = ScrappedEvent(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    status = when (status) {
        ScrappedEventResponse.Status.ENDED -> ScrappedEvent.Status.ENDED
        ScrappedEventResponse.Status.UPCOMING -> ScrappedEvent.Status.UPCOMING
        ScrappedEventResponse.Status.IN_PROGRESS -> ScrappedEvent.Status.IN_PROGRESS
    },
    tags = tags,
    posterUrl = posterUrl,
    dDay = dDay,
    eventStatus = when (applyStatus) {
        ScrappedEventResponse.Status.ENDED -> ScrappedEvent.Status.ENDED
        ScrappedEventResponse.Status.UPCOMING -> ScrappedEvent.Status.UPCOMING
        ScrappedEventResponse.Status.IN_PROGRESS -> ScrappedEvent.Status.IN_PROGRESS
    },
    applyRemainingDays = applyRemainingDays,
    eventMode = when (eventMode) {
        ScrappedEventResponse.EventMode.ONLINE -> ScrappedEvent.EventMode.ONLINE
        ScrappedEventResponse.EventMode.OFFLINE -> ScrappedEvent.EventMode.OFFLINE
        ScrappedEventResponse.EventMode.ON_OFFLINE -> ScrappedEvent.EventMode.ON_OFFLINE
    },
    paymentType = when (paymentType) {
        ScrappedEventResponse.PaymentType.FREE -> ScrappedEvent.PaymentType.FREE
        ScrappedEventResponse.PaymentType.PAID -> ScrappedEvent.PaymentType.PAID
        ScrappedEventResponse.PaymentType.PAID_OR_FREE -> ScrappedEvent.PaymentType.PAID_OR_FREE
    },
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}

fun List<ScrappedEventResponse>.toData(): List<ScrappedEvent> = map {
    it.toData()
}
