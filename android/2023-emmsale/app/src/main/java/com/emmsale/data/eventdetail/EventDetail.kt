package com.emmsale.data.eventdetail

data class EventDetail(
    val id: Long,
    val name: String,
    val status: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val informationUrl: String,
    val tags: List<String>,
    val imageUrl: String,
    val remainingDays: Int,
    val type: String,
)
