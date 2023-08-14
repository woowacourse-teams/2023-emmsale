package com.emmsale.data.recruitment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentPostingRequestBody(
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("content")
    val content: String,
)
