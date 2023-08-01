package com.emmsale.data.event

import java.time.LocalDateTime

data class Event(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: String,
    val tags: List<String>,
    val posterUrl: String?,
    val dDay: Int,
)
