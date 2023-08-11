package com.emmsale.data.eventdetail

data class EventDetail(
    val id: Long,
    val name: String,
    val informationUrl: String,
    val startDate: String,
    val endDate: String,
    val applyStartDate: String,
    val applyEndDate: String,
    val location: String,
    val status: String,
    val applyStatus: String,
    val tags: List<String>,
    val imageUrl: String?,
    val remainingDays: Int,
    val applyRemainingDays: Int,
    val type: String,
)
