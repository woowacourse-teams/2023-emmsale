package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScrappedEventResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("endDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("applyStartDate")
    val applyStartDate: String,
    @SerialName("applyEndDate")
    val applyEndDate: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val posterUrl: String? = null,
    @SerialName("eventMode")
    val eventMode: EventMode,
    @SerialName("paymentType")
    val paymentType: PaymentType,
) {
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
