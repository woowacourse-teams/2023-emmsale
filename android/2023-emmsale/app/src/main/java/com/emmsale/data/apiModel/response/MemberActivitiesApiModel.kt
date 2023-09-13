package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberActivitiesApiModel(
    @SerialName("activityType")
    val activityType: String,
    @SerialName("memberActivityResponses")
    val memberActivityResponses: List<ActivityApiModel>,
)
