package com.emmsale.data.scrap.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScrappedEventRequestBody(
    @Serializable
    val eventId: Long,
)
