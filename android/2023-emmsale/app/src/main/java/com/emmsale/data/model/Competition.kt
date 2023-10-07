package com.emmsale.data.model

import java.time.LocalDateTime

data class Competition(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applicationStartDate: LocalDateTime,
    val applicationEndDate: LocalDateTime,
    val tags: List<String>,
    val posterUrl: String?,
    val onOfflineMode: OnOfflineMode,
    val paymentType: PaymentType,
) {
    val progressStatus: EventProgressStatus
        get() = EventProgressStatus.create(startDate, endDate)

    val applicationStatus: EventApplicationStatus
        get() = EventApplicationStatus.create(applicationStartDate, applicationEndDate)
}
