package com.emmsale.data.comment.mapper

import com.emmsale.data.comment.Comment
import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.CommentFamilyApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JvmName("mapCommentFamilyApiModelsToData")
fun List<CommentFamilyApiModel>.toData(): List<Comment> = map(CommentFamilyApiModel::toData)

fun CommentFamilyApiModel.toData() = Comment(
    id = parentComment.commentId,
    eventId = 1L,
    eventName = "댓글 api model 매핑 로직 변경해야함",
    authorId = parentComment.memberId,
    authorName = parentComment.memberName,
    authorImageUrl = parentComment.memberImageUrl,
    content = parentComment.content,
    createdAt = LocalDateTime.parse(parentComment.createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(parentComment.updatedAt, dateTimeFormatter),
    deleted = parentComment.deleted,
    childComments = childComments.toData(),
)

@JvmName("mapCommentApiModelToData")
fun List<CommentApiModel>.toData(): List<Comment> = map(CommentApiModel::toData)

fun CommentApiModel.toData() = Comment(
    id = commentId,
    eventId = 1L,
    eventName = "댓글 api model 매핑 로직 변경해야함",
    authorId = memberId,
    authorName = memberName,
    authorImageUrl = memberImageUrl,
    content = content,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
    deleted = deleted,
    childComments = listOf(),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
