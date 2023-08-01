package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberWithoutActivitiesApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("imageUrl")
    val imageUrl: String,
)
