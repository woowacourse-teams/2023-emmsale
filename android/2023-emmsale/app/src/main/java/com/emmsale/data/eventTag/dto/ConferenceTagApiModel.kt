package com.emmsale.data.eventTag.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceTagApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
