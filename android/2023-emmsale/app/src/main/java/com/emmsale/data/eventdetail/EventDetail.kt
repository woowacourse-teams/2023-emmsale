package com.emmsale.data.eventdetail

import java.time.LocalDateTime

data class EventDetail(
    val id: Long,
    val name: String,
    val informationUrl: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applyStartDate: LocalDateTime,
    val applyEndDate: LocalDateTime,
    val location: String,
    val status: String,
    val applyStatus: String,
    val tags: List<String>,
    val imageUrl: String?,
    val remainingDays: Int,
    val applyRemainingDays: Int,
    val type: String,
)
