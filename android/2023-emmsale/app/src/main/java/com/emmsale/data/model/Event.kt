package com.emmsale.data.model

import java.time.LocalDateTime

data class Event(
    // val id: Long = DEFAULT_ID,
    // val name: String = "",
    // val informationUrl: String = "",
    // val organization: String? = null,
    // val startDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    // val endDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    // val applyingStartDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    // val applyingEndDate: LocalDateTime = DEFAULT_LOCAL_DATE_TIME,
    // val location: String = "",
    // val tags: List<String> = emptyList(),
    // val posterImageUrl: String? = "",
    // val paymentType: PaymentType = DEFAULT_PAYMENT_TYPE,
    // val onOfflineMode: OnOfflineMode = DEFAULT_ON_OFFLINE_MODE,
    // val type: String = "",
    // val detailImageUrls: List<String> = emptyList(),
    val id: Long = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val name: String = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val informationUrl: String = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val organization: String? = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val startDate: LocalDateTime = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val endDate: LocalDateTime = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val applyingStartDate: LocalDateTime = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val applyingEndDate: LocalDateTime = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val location: String = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val tags: List<String> = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val posterImageUrl: String? = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val paymentType: PaymentType = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val onOfflineMode: OnOfflineMode = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val type: String = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
    val detailImageUrls: List<String> = throw AssertionError("초기화되지 않은 프로퍼티 입니다."),
) {
    val progressStatus: EventProgressStatus
        get() = EventProgressStatus.create(startDate, endDate)

    val applicationStatus: EventApplyingStatus
        get() = EventApplyingStatus.create(applyingStartDate, applyingEndDate)

    companion object {
        private const val DEFAULT_ID: Long = -1
        private val DEFAULT_LOCAL_DATE_TIME = LocalDateTime.MAX
        private val DEFAULT_PAYMENT_TYPE = PaymentType.PAID
        private val DEFAULT_ON_OFFLINE_MODE = OnOfflineMode.ON_OFFLINE
    }
}
