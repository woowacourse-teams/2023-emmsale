package com.emmsale.data.apiModel.request

import kotlinx.serialization.Serializable

@Serializable
data class ScrappedEventCreateRequest(
    @Serializable
    val eventId: Long,
)
