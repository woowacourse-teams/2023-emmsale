package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveCommentRequestBody(
    @SerialName("content")
    val content: String,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("parentId")
    val parentId: Long? = null,
)
