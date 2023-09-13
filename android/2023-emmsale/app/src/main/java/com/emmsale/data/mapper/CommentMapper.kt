package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.apiModel.response.CommentResponse
import com.emmsale.data.model.Comment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JvmName("mapCommentFamilyApiModelsToData")
fun List<CommentFamilyApiModel>.toData(): List<Comment> = map(CommentFamilyApiModel::toData)

fun CommentFamilyApiModel.toData() = Comment(
    id = parentComment.commentId,
    eventId = 1L,
    eventName = parentComment.eventName,
    authorId = parentComment.memberId,
    authorName = parentComment.memberName,
    authorImageUrl = parentComment.memberImageUrl,
    parentId = parentComment.parentId,
    content = parentComment.content,
    createdAt = LocalDateTime.parse(parentComment.createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(parentComment.updatedAt, dateTimeFormatter),
    deleted = parentComment.deleted,
    childComments = childComments.toData(),
)

@JvmName("mapCommentApiModelToData")
fun List<CommentResponse>.toData(): List<Comment> = map(CommentResponse::toData)

fun CommentResponse.toData() = Comment(
    id = commentId,
    eventId = 1L,
    eventName = eventName,
    authorId = memberId,
    authorName = memberName,
    authorImageUrl = memberImageUrl,
    parentId = parentId,
    content = content,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
    deleted = deleted,
    childComments = listOf(),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
