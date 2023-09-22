package com.emmsale.data.model

import java.time.LocalDateTime

data class EventDetail(
    val id: Long,
    val name: String,
    val informationUrl: String,
    val organization: String? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applyStartDate: LocalDateTime,
    val applyEndDate: LocalDateTime,
    val location: String,
    val status: String,
    val applyStatus: String,
    val tags: List<String>,
    val posterImageUrl: String?,
    val remainingDays: Int,
    val applyRemainingDays: Int,
    val paymentType: Conference.PaymentType,
    val type: String,
) {
    companion object {
        val EmptyData: EventDetail = EventDetail(
            id = -1,
            name = "",
            informationUrl = "",
            organization = null,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now(),
            applyStartDate = LocalDateTime.MAX,
            applyEndDate = LocalDateTime.MAX,
            location = "",
            status = "",
            applyStatus = "",
            tags = emptyList(),
            posterImageUrl = null,
            remainingDays = 1,
            applyRemainingDays = 1,
            paymentType = Conference.PaymentType.FREE,
            type = "",
        )
    }
}
