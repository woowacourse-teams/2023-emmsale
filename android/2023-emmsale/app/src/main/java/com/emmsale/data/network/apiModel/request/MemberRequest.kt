package com.emmsale.data.network.apiModel.request

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
data class MemberActivitiesAddRequest(
    @SerialName("activityIds")
    val activityIds: List<Long>,
)

@Serializable
data class MemberDescriptionUpdateRequest(
    @SerialName("description")
    val description: String,
)

@Serializable
data class MemberBlockCreateRequest(
    @SerialName("blockMemberId")
    val blockMemberId: Long,
)