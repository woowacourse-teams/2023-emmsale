package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberCreateRequest(
    @SerialName("name")
    val name: String,
    @SerialName("activityIds")
    val activityIds: List<Long>,
)

@Serializable
data class MemberActivitiesUpdateRequest(
    @SerialName("activityIds")
    val activityIds: List<Long>,
)

@Serializable
data class MemberOpenProfileUrlUpdateRequest(
    @SerialName("openProfileUrl")
    val openProfileUrl: String,
)

@Serializable
data class MemberDescriptionUpdateRequest(
    @SerialName("description")
    val description: String,
)
