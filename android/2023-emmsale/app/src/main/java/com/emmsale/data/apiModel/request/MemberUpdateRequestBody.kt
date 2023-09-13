package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateRequestBody(
    @SerialName("name")
    val name: String,
    @SerialName("activityIds")
    val activityIds: List<Long>,
)
