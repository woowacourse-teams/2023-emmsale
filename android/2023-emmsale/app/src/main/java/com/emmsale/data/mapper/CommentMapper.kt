package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.network.apiModel.response.CommentResponse
import com.emmsale.model.Comment
import com.emmsale.model.Feed
import com.emmsale.model.Member

@JvmName("mapCommentFamilyApiModelsToData")
fun List<CommentFamilyApiModel>.toData(): List<Comment> = map(CommentFamilyApiModel::toData)

fun CommentFamilyApiModel.toData() = Comment(
    id = parentComment.commentId,
    feed = Feed(
        id = parentComment.feedId,
        title = parentComment.feedTitle,
    ),
    writer = Member(
        id = parentComment.memberId,
        name = parentComment.memberName,
        profileImageUrl = parentComment.memberImageUrl,
    ),
    parentCommentId = parentComment.parentId,
    content = parentComment.content,
    createdAt = parentComment.createdAt,
    updatedAt = parentComment.updatedAt,
    isDeleted = parentComment.deleted,
    childComments = childComments.toData(),
)

@JvmName("mapCommentApiModelToData")
fun List<CommentResponse>.toData(): List<Comment> = map(CommentResponse::toData)

fun CommentResponse.toData() = Comment(
    id = commentId,
    feed = Feed(
        id = feedId,
        title = feedTitle,
    ),
    writer = Member(
        id = memberId,
        name = memberName,
        profileImageUrl = memberImageUrl,
    ),
    parentCommentId = parentId,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = deleted,
    childComments = listOf(),
)
