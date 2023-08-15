package com.emmsale.data.event.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompetitionApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("endDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("status")
    val status: String,
    @SerialName("applyStatus")
    val applyStatus: String,
    @SerialName("imageUrl")
    val posterUrl: String? = null,
    @SerialName("remainingDays")
    val dDay: Int,
    @SerialName("applyRemainingDays")
    val applyRemainingDays: Int,
)
