package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.EventResponse
import com.emmsale.data.model.Event
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType

fun List<EventResponse>.toData(): List<Event> = map { it.toData() }

fun EventResponse.toData(): Event = Event(
    id = id,
    name = name,
    organization = organization,
    informationUrl = informationUrl,
    startDate = startDate,
    endDate = endDate,
    applyingStartDate = applyStartDate,
    applyingEndDate = applyEndDate,
    location = location,
    tags = tags.toData(),
    posterImageUrl = thumbnailUrl?.let { BuildConfig.IMAGE_URL_PREFIX + it } ?: "",
    type = type,
    paymentType = paymentType.toPaymentType(),
    onOfflineMode = onOfflineMode.toData(),
    detailImageUrls = imageUrls.map { BuildConfig.IMAGE_URL_PREFIX + it },
)

private fun EventResponse.PaymentType.toPaymentType(): PaymentType = when (this) {
    EventResponse.PaymentType.PAID -> PaymentType.PAID
    EventResponse.PaymentType.FREE -> PaymentType.FREE
    EventResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
}

private fun EventResponse.OnOfflineMode.toData(): OnOfflineMode = when (this) {
    EventResponse.OnOfflineMode.ONLINE -> OnOfflineMode.ONLINE
    EventResponse.OnOfflineMode.OFFLINE -> OnOfflineMode.OFFLINE
    EventResponse.OnOfflineMode.ON_OFFLINE -> OnOfflineMode.ON_OFFLINE
}
