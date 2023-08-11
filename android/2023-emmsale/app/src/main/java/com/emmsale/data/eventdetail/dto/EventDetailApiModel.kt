package com.emmsale.data.eventdetail.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("informationUrl")
    val informationUrl: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("applyStartDate")
    val applyStartDate: String,
    @SerialName("applyEndDate")
    val applyEndDate: String,
    @SerialName("location")
    val location: String,
    @SerialName("status")
    val status: String,
    @SerialName("applyStatus")
    val applyStatus: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("remainingDays")
    val remainingDays: Int,
    @SerialName("applyRemainingDays")
    val applyRemainingDays: Int,
    @SerialName("type")
    val type: String,
)
