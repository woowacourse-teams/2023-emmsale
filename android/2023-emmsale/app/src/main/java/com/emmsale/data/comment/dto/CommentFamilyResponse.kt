package com.emmsale.data.comment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentFamilyResponse(
    @SerialName("parentComment")
    val parentComment: CommentResponse,
    @SerialName("childComments")
    val childComments: List<CommentResponse>
)
