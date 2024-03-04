package com.emmsale.data.network.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("githubUrl")
    val githubUrl: String = "",
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("activities")
    val activities: List<ActivityResponse> = emptyList(),
)
