package com.emmsale.data.recruitment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentDeletionRequestBody(
    @SerialName("content")
    val content: String,
)
