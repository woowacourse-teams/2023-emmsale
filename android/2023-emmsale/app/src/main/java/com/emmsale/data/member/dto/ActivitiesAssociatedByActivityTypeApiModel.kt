package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivitiesAssociatedByActivityTypeApiModel(
    @SerialName("activityType")
    val activityType: String,
    @SerialName("memberActivityResponses")
    val memberActivityResponses: List<ActivityApiModel>,
)

@Serializable
data class ActivityApiModel(
    val id: Long,
    val name: String,
)
