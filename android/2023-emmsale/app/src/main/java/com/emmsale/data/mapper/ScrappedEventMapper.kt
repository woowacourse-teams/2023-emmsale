package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType
import com.emmsale.data.model.ScrappedEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun ScrappedEventResponse.toData(): ScrappedEvent = ScrappedEvent(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    applicationStartDate = parseDate(applyStartDate),
    applicationEndDate = parseDate(applyEndDate),
    tags = tags,
    posterUrl = posterUrl?.let { BuildConfig.IMAGE_URL_PREFIX + it },
    onOfflineMode = when (eventMode) {
        ScrappedEventResponse.EventMode.ONLINE -> OnOfflineMode.ONLINE
        ScrappedEventResponse.EventMode.OFFLINE -> OnOfflineMode.OFFLINE
        ScrappedEventResponse.EventMode.ON_OFFLINE -> OnOfflineMode.ON_OFFLINE
    },
    paymentType = when (paymentType) {
        ScrappedEventResponse.PaymentType.FREE -> PaymentType.FREE
        ScrappedEventResponse.PaymentType.PAID -> PaymentType.PAID
        ScrappedEventResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
    },
)

private fun parseDate(date: String): LocalDateTime {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    return LocalDateTime.parse(date, dateTimeFormatter)
}

fun List<ScrappedEventResponse>.toData(): List<ScrappedEvent> = map {
    it.toData()
}
