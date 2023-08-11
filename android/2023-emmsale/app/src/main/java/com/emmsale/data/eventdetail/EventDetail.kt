package com.emmsale.data.eventdetail

import java.time.LocalDateTime

data class EventDetail(
    val id: Long,
    val name: String,
    val status: String,
    val location: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val informationUrl: String,
    val tags: List<String>,
    val postImageUrl: String?,
    val remainingDays: Int,
    val type: String,
)
