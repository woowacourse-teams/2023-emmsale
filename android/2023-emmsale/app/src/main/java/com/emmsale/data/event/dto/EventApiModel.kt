package com.emmsale.data.event.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EventApiModel(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("status")
    val status: String,
    @SerialName("tags")
    val tags: List<String>
)
