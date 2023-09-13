package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScrappedEventApiModel(
    @SerialName("scrapId")
    val scrapId: Long,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("status")
    val status: String,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("tags")
    val tags: List<String> = listOf(),
)
