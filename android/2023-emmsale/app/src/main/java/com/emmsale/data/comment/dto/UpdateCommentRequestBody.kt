package com.emmsale.data.comment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCommentRequestBody(
    @SerialName("content")
    val content: String,
)
