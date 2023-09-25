package com.emmsale.data.model

import java.time.LocalDateTime

data class Competition(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: EventStatus,
    val tags: List<String>,
    val posterUrl: String?,
    val remainingDays: Int,
    val eventStatus: EventStatus,
    val applyRemainingDays: Int,
    val onOfflineMode: OnOfflineMode,
    val paymentType: PaymentType,
)
