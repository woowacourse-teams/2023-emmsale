package com.emmsale.data.comment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentFamilyApiModel(
    @SerialName("parentComment")
    val parentComment: CommentApiModel,
    @SerialName("childComments")
    val childComments: List<CommentApiModel>,
)
