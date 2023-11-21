package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("activityType")
    val activityType: String,
)

@Serializable
data class MemberActivitiesResponse(
    @SerialName("activityType")
    val activityType: String,
    @SerialName("memberActivityResponses")
    val memberActivityResponses: List<ActivityResponse>,
)
