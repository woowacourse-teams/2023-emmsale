package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.EventDetailResponse
import com.emmsale.data.model.EventDetail
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
    status = status.toKoreanStatus(),
    applyStatus = applyStatus.toKoreanApplyStatus(),
    tags = tags,
    posterImageUrl = imageUrl ?: "",
    remainingDays = remainingDays,
    applyRemainingDays = applyRemainingDays,
    type = type,
    paymentType = when (paymentType) {
        "유료" -> PaymentType.PAID
        "무료" -> PaymentType.FREE
        "유무료" -> PaymentType.PAID_OR_FREE
        else -> throw IllegalArgumentException("행사 상세 응답 비용 정보 매핑 실패")
    },
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}

private fun String.toKoreanStatus(): String = when (this) {
    "IN_PROGRESS" -> "진행 중"
    "UPCOMING" -> "진행 예정"
    "ENDED" -> "마감"
    else -> throw IllegalArgumentException("행사 상세 정보의 상태를 변환하는 데 실패했습니다. api 스펙을 다시 확인해주세요.")
}

private fun String.toKoreanApplyStatus(): String = when (this) {
    "IN_PROGRESS" -> "진행 중"
    "UPCOMING" -> "진행 예정"
    "ENDED" -> "마감"
    else -> throw IllegalArgumentException("행사 상세 정보의 신청 상태를 변환하는 데 실패했습니다. api 스펙을 다시 확인해주세요.")
}
