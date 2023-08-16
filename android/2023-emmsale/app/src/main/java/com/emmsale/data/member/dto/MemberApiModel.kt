package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberApiModel(
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
    @SerialName("openProfileUrl")
    val openProfileUrl: String = "",
)
