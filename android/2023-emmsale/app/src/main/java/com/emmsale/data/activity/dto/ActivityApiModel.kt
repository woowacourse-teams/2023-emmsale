package com.emmsale.data.activity.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
