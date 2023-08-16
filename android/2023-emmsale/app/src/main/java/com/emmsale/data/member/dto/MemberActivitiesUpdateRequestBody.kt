package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberActivitiesUpdateRequestBody(
    @SerialName("activityIds")
    val activityIds: List<Long>,
)
