package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.model.Competition
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"

fun List<CompetitionResponse>.toData(): List<Competition> = map { it.toData() }

fun CompetitionResponse.toData(): Competition = Competition(
    id = id,
    name = name,
    startDate = parseDate(startDate),
    endDate = parseDate(endDate),
    applicationStartDate = parseDate(applyStartDate),
    applicationEndDate = parseDate(applyEndDate),
    tags = tags,
    posterUrl = posterUrl?.let { BuildConfig.IMAGE_URL_PREFIX + it },
    onOfflineMode = when (onOfflineMode) {
        CompetitionResponse.OnOfflineMode.ONLINE -> OnOfflineMode.ONLINE
        CompetitionResponse.OnOfflineMode.OFFLINE -> OnOfflineMode.OFFLINE
        CompetitionResponse.OnOfflineMode.ON_OFFLINE -> OnOfflineMode.ON_OFFLINE
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
