package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("eventStartDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("eventEndDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("applyStartDate")
    val applyStartDate: String, // format : "2023:09:03:12:00:00"
    @SerialName("applyEndDate")
    val applyEndDate: String, // format : "2023:09:03:12:00:00"
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("thumbnailUrl")
    val posterUrl: String? = null,
    @SerialName("eventMode")
    val onOfflineMode: OnOfflineMode,
    @SerialName("paymentType")
    val paymentType: PaymentType,
) {
    enum class OnOfflineMode {
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
    @SerialName("eventStartDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("eventEndDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("applyStartDate")
    val applyStartDate: String, // format : "2023:09:03:12:00:00"
    @SerialName("applyEndDate")
    val applyEndDate: String, // format : "2023:09:03:12:00:00"
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("thumbnailUrl")
    val posterUrl: String? = null,
    @SerialName("eventMode")
    val onOfflineMode: OnOfflineMode,
    @SerialName("paymentType")
    val paymentType: PaymentType,
) {
    enum class OnOfflineMode {
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
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerialName("type")
    val type: String,
    @SerialName("imageUrls")
    val imageUrls: List<String> = emptyList(),
    @SerialName("organization")
    val organization: String,
    @SerialName("paymentType")
    val paymentType: PaymentType,
    @SerialName("eventMode")
    val onOfflineMode: OnOfflineMode,
) {
    enum class PaymentType {
        @SerialName("유료")
        PAID,

        @SerialName("무료")
        FREE,

        @SerialName("유무료")
        PAID_OR_FREE,
    }

    enum class OnOfflineMode {
        @SerialName("온라인")
        ONLINE,

        @SerialName("오프라인")
        OFFLINE,

        @SerialName("온오프라인")
        ON_OFFLINE,
    }
}
