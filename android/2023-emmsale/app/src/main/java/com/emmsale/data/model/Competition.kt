package com.emmsale.data.model

import java.time.LocalDateTime

data class Competition(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: Status,
    val tags: List<String>,
    val posterUrl: String?,
    val dDay: Int,
    val eventStatus: Status,
    val applyRemainingDays: Int,
    val eventMode: EventMode,
    val paymentType: PaymentType,
) {
    enum class Status {
        IN_PROGRESS, UPCOMING, ENDED,
    }

    enum class EventMode {
        ONLINE, OFFLINE, ON_OFFLINE,
    }

    enum class PaymentType {
        PAID, FREE, PAID_OR_FREE,
    }
}
