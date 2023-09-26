package com.emmsale.data.model

import java.time.LocalDateTime

data class ScrappedEvent(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applyStatus: EventStatus,
    val tags: List<String>,
    val posterUrl: String?,
    val eventStatus: EventStatus,
    val onOfflineMode: OnOfflineMode,
    val paymentType: PaymentType,
)
