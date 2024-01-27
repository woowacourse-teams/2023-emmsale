package com.emmsale.data.apiModel.response

import com.emmsale.data.apiModel.serializer.DateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("informationUrl")
    val informationUrl: String,
    @SerialName("startDate")
    @Serializable(with = DateTimeSerializer::class)
    val startDate: LocalDateTime,
    @SerialName("endDate")
    @Serializable(with = DateTimeSerializer::class)
    val endDate: LocalDateTime,
    @SerialName("applyStartDate")
    @Serializable(with = DateTimeSerializer::class)
    val applyStartDate: LocalDateTime,
    @SerialName("applyEndDate")
    @Serializable(with = DateTimeSerializer::class)
    val applyEndDate: LocalDateTime,
    @SerialName("location")
    val location: String,
    @SerialName("tags")
    val tags: List<EventTagResponse>,
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
