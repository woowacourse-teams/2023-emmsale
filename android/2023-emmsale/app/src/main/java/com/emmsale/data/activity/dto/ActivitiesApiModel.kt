package com.emmsale.data.activity.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivitiesApiModel(
    @SerialName("activityType")
    val category: String = "-",
    @SerialName("activityResponses")
    val activities: List<ActivityApiModel> = emptyList(),
)
