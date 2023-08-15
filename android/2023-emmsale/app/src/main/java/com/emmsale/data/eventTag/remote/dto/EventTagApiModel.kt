package com.emmsale.data.eventTag.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventTagApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
