package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("content")
    val content: String,
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("parentId")
    val parentId: Long?,
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("feedTitle")
    val feedTitle: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("memberImageUrl")
    val memberImageUrl: String,
    @SerialName("memberName")
    val memberName: String,
    @SerialName("deleted")
    val deleted: Boolean,
)

@Serializable
data class CommentFamilyApiModel(
    @SerialName("parentComment")
    val parentComment: CommentResponse,
    @SerialName("childComments")
    val childComments: List<CommentResponse>,
)

@Serializable
data class CommentReportResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("reporterId")
    val reporterId: Long,
    @SerialName("reportedId")
    val reportedId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("contentId")
    val contentId: Long,
    @SerialName("createdAt")
    val createdAt: String,
)
