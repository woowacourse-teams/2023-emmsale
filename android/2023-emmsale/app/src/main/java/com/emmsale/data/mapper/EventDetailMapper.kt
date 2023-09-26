package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.EventDetailResponse
import com.emmsale.data.model.EventDetail
import com.emmsale.data.model.EventStatus
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
    applyStartDate = applyStartDate.toLocalDateTime(),
    applyEndDate = applyEndDate.toLocalDateTime(),
    location = location,
    eventStatus = status.toEventStatus(),
    applyStatus = applyStatus.toEventStatus(),
    tags = tags,
    posterImageUrl = imageUrl ?: "",
    remainingDays = remainingDays,
    applyRemainingDays = applyRemainingDays,
    type = type,
    paymentType = paymentType.toPaymentType(),
    imageUrls = imageUrls.map { url -> "https://d3ms3abrjbgefs.cloudfront.net/dev/" + url },
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}

private fun EventDetailResponse.Status.toEventStatus(): EventStatus = when (this) {
    EventDetailResponse.Status.IN_PROGRESS -> EventStatus.IN_PROGRESS
    EventDetailResponse.Status.UPCOMING -> EventStatus.UPCOMING
    EventDetailResponse.Status.ENDED -> EventStatus.ENDED
}

private fun EventDetailResponse.PaymentType.toPaymentType(): PaymentType = when (this) {
    EventDetailResponse.PaymentType.PAID -> PaymentType.PAID
    EventDetailResponse.PaymentType.FREE -> PaymentType.FREE
    EventDetailResponse.PaymentType.PAID_OR_FREE -> PaymentType.PAID_OR_FREE
}
