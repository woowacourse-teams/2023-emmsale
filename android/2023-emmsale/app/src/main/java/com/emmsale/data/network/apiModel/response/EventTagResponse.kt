package com.emmsale.data.network.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventTagResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
