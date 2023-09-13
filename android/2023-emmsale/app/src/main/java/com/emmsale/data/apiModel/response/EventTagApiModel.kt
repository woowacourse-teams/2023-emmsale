package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventTagApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
