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
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}

private fun String.toEventStatus(): EventStatus = when (this) {
    "IN_PROGRESS" -> EventStatus.IN_PROGRESS
    "UPCOMING" -> EventStatus.UPCOMING
    "ENDED" -> EventStatus.ENDED
    else -> throw IllegalArgumentException("행사 상세 정보의 신청 상태를 변환하는 데 실패했습니다. api 스펙을 다시 확인해주세요.")
}

private fun String.toPaymentType(): PaymentType = when (this) {
    "유료" -> PaymentType.PAID
    "무료" -> PaymentType.FREE
    "유무료" -> PaymentType.PAID_OR_FREE
    else -> throw IllegalArgumentException("행사 상세 응답 비용 정보 매핑 실패")
}
