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
    val eventStatus: EventStatus,
    val applyStatus: EventStatus,
    val tags: List<String>,
    val posterImageUrl: String?,
    val paymentType: PaymentType,
    val type: String,
    val detailImageUrls: List<String>,
)
