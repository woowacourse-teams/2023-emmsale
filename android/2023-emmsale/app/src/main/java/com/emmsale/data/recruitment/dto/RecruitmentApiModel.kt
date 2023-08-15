package com.emmsale.data.recruitment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("content")
    val content: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)
