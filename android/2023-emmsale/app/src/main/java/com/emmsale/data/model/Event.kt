package com.emmsale.data.model

import java.time.LocalDateTime

data class Event(
    val id: Long,
    val name: String = "",
    val informationUrl: String = "",
    val organization: String? = null,
    val startDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    val endDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    val applyingStartDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    val applyingEndDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    val location: String = "",
    val tags: List<String> = emptyList(),
    val posterImageUrl: String? = "",
    val paymentType: PaymentType = DEFAULT_PAYMENT_TYPE,
    val onOfflineMode: OnOfflineMode = DEFAULT_ON_OFFLINE_MODE,
    val type: String = "",
    val detailImageUrls: List<String> = emptyList(),
) {
    val progressStatus: EventProgressStatus
        get() = EventProgressStatus.create(startDate, endDate)

    val applicationStatus: EventApplyingStatus
        get() = EventApplyingStatus.create(applyingStartDate, applyingEndDate)

    companion object {
        private val DEFAULT_LOCAL_DATE_TIME = LocalDateTime.MAX
        private val DEFAULT_PAYMENT_TYPE = PaymentType.PAID
        private val DEFAULT_ON_OFFLINE_MODE = OnOfflineMode.ON_OFFLINE
    }
}
