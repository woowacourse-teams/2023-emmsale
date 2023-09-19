package com.emmsale.data.model

import java.time.LocalDateTime

data class Conference(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: ConferenceStatus,
    val tags: List<String>,
    val posterUrl: String?,
    val dDay: Int,
    val eventApplyStatus: EventApplyStatus,
    val applyRemainingDays: Int,
    val isOnline: Boolean,
    val isFree: Boolean,
)
