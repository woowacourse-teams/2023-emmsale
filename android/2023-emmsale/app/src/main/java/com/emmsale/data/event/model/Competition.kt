package com.emmsale.data.event.model

import com.emmsale.data.competitionStatus.CompetitionStatus
import com.emmsale.data.eventApplyStatus.EventApplyStatus
import java.time.LocalDateTime

data class Competition(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: CompetitionStatus,
    val tags: List<String>,
    val posterUrl: String?,
    val dDay: Int,
    val eventApplyStatus: EventApplyStatus,
    val applyRemainingDays: Int,
    val isOnline: Boolean,
)
