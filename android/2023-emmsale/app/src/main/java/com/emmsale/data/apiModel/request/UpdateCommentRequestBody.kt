package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCommentRequestBody(
    @SerialName("content")
    val content: String,
)
