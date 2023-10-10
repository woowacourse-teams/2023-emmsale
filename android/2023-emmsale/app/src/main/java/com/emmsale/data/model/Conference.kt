package com.emmsale.data.model

import java.time.LocalDateTime

data class Conference(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applyingStartDate: LocalDateTime,
    val applyingEndDate: LocalDateTime,
    val tags: List<String>,
    val posterUrl: String?,
    val onOfflineMode: OnOfflineMode,
    val paymentType: PaymentType,
) {
    val progressStatus: EventProgressStatus
        get() = EventProgressStatus.create(startDate, endDate)

    val applicationStatus: EventApplyingStatus
        get() = EventApplyingStatus.create(applyingStartDate, applyingEndDate)
}
