package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceResponse(
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
    @SerialName("isOnline")
    val isOnline: Boolean = false,
    @SerialName("isFree")
    val isFree: Boolean = false,
)

@Serializable
data class CompetitionResponse(
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
    @SerialName("isFree")
    val isFree: Boolean = false,
    @SerialName("isOnline")
    val isOnline: Boolean = false,
)

@Serializable
data class EventDetailResponse(
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
