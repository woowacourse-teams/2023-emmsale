package com.emmsale.data.apiModel.request

import kotlinx.serialization.Serializable

@Serializable
data class ScrappedEventRequestBody(
    @Serializable
    val eventId: Long,
)
