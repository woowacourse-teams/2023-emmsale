package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
)
