package com.emmsale.data.model

import java.time.LocalDateTime

data class EventDetail(
    val id: Long,
    val name: String,
    val informationUrl: String,
    val organization: String? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val applicationStartDate: LocalDateTime,
    val applicationEndDate: LocalDateTime,
    val location: String,
    val tags: List<String>,
    val posterImageUrl: String?,
    val paymentType: PaymentType,
    val type: String,
    val detailImageUrls: List<String>,
) {
    val progressStatus: EventProgressStatus
        get() = EventProgressStatus.create(startDate, endDate)

    val applicationStatus: EventApplicationStatus
        get() = EventApplicationStatus.create(applicationStartDate, applicationEndDate)
}
