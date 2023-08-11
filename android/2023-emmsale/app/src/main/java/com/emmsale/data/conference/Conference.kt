package com.emmsale.data.conference

import com.emmsale.data.applyStatus.ApplyStatus
import com.emmsale.data.conferenceStatus.ConferenceStatus
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
    val applyStatus: ApplyStatus,
    val applyRemainingDays: Int,
)
