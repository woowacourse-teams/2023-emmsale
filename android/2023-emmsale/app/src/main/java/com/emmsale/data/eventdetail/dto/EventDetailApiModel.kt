package com.emmsale.data.eventdetail.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("status")
    val status: String,
    @SerialName("location")
    val location: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("informationUrl")
    val informationUrl: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("remainingDays")
    val remainingDays: Int,
    @SerialName("type")
    val type: String,
)
