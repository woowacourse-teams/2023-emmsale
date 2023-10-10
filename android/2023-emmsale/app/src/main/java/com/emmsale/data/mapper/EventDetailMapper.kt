package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.EventDetailResponse
import com.emmsale.data.model.EventDetail
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun EventDetailResponse.toData(): EventDetail = EventDetail(
    id = id,
    name = name,
    organization = organization,
    informationUrl = informationUrl,
    startDate = startDate.toLocalDateTime(),
    endDate = endDate.toLocalDateTime(),
    applyingStartDate = applyStartDate.toLocalDateTime(),
    applyingEndDate = applyEndDate.toLocalDateTime(),
    location = location,
    tags = tags,
    posterImageUrl = thumbnailUrl?.let { BuildConfig.IMAGE_URL_PREFIX + it } ?: "",
    type = type,
    paymentType = paymentType.toPaymentType(),
    onOfflineMode = onOfflineMode.toData(),
    detailImageUrls = imageUrls.map { BuildConfig.IMAGE_URL_PREFIX + it },
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}

private fun EventDetailResponse.PaymentType.toPaymentType(): PaymentType = when (this) {
    EventDetailResponse.PaymentType.PAID -> PaymentType.PAID
    EventDetailResponse.PaymentType.FREE -> PaymentType.FREE
    EventDetailResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
}

private fun EventDetailResponse.OnOfflineMode.toData(): OnOfflineMode = when (this) {
    EventDetailResponse.OnOfflineMode.ONLINE -> OnOfflineMode.ONLINE
    EventDetailResponse.OnOfflineMode.OFFLINE -> OnOfflineMode.OFFLINE
    EventDetailResponse.OnOfflineMode.ON_OFFLINE -> OnOfflineMode.ON_OFFLINE
}
