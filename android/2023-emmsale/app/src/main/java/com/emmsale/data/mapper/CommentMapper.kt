package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.apiModel.response.CommentResponse
import com.emmsale.data.model.Comment
import com.emmsale.data.model.Feed
import com.emmsale.data.model.Member
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    createdAt = LocalDateTime.parse(parentComment.createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(parentComment.updatedAt, dateTimeFormatter),
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
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
    isDeleted = deleted,
    childComments = listOf(),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
