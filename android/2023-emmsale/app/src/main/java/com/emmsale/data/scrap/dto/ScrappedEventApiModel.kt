package com.emmsale.data.scrap.dto

import kotlinx.serialization.SerialName

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
    val imageUrl: String,
    @SerialName("tags")
    val tags: List<String> = listOf(),
)
