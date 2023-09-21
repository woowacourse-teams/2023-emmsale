package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("endDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("status")
    val status: Status,
    @SerialName("applyStatus")
    val applyStatus: Status,
    @SerialName("imageUrl")
    val posterUrl: String? = null,
    @SerialName("remainingDays")
    val dDay: Int,
    @SerialName("applyRemainingDays")
    val applyRemainingDays: Int,
    @SerialName("eventMode")
    val eventMode: EventMode,
    @SerialName("paymentType")
    val paymentType: PaymentType,
) {
    enum class Status {
        @SerialName("IN_PROGRESS")
        IN_PROGRESS,

        @SerialName("UPCOMING")
        UPCOMING,

        @SerialName("ENDED")
        ENDED,
    }

    enum class EventMode {
        @SerialName("온라인")
        ONLINE,

        @SerialName("오프라인")
        OFFLINE,

        @SerialName("온오프라인")
        ON_OFFLINE,
    }

    enum class PaymentType {
        @SerialName("유료")
        PAID,

        @SerialName("무료")
        FREE,

        @SerialName("유무료")
        PAID_OR_FREE,
    }
}

@Serializable
data class CompetitionResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("endDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("status")
    val status: Status,
    @SerialName("applyStatus")
    val applyStatus: Status,
    @SerialName("imageUrl")
    val posterUrl: String? = null,
    @SerialName("remainingDays")
    val dDay: Int,
    @SerialName("applyRemainingDays")
    val applyRemainingDays: Int,
    @SerialName("eventMode")
    val eventMode: EventMode,
    @SerialName("paymentType")
    val paymentType: PaymentType,
) {
    enum class Status {
        @SerialName("IN_PROGRESS")
        IN_PROGRESS,

        @SerialName("UPCOMING")
        UPCOMING,

        @SerialName("ENDED")
        ENDED,
    }

    enum class EventMode {
        @SerialName("온라인")
        ONLINE,

        @SerialName("오프라인")
        OFFLINE,

        @SerialName("온오프라인")
        ON_OFFLINE,
    }

    enum class PaymentType {
        @SerialName("유료")
        PAID,

        @SerialName("무료")
        FREE,

        @SerialName("유무료")
        PAID_OR_FREE,
    }
}

@Serializable
data class EventDetailResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("informationUrl")
    val informationUrl: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("applyStartDate")
    val applyStartDate: String,
    @SerialName("applyEndDate")
    val applyEndDate: String,
    @SerialName("location")
    val location: String,
    @SerialName("status")
    val status: String,
    @SerialName("applyStatus")
    val applyStatus: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("remainingDays")
    val remainingDays: Int,
    @SerialName("applyRemainingDays")
    val applyRemainingDays: Int,
    @SerialName("type")
    val type: String,
    @SerialName("imageUrls")
    val imageUrls: List<String> = emptyList(),
)
