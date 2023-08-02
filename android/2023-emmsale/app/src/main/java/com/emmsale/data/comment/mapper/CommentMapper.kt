package com.emmsale.data.comment.mapper

import com.emmsale.data.comment.Comment
import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.member.Member1
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CommentFamilyApiModel.toData(
    parentCommentAuthor: Member1,
    childCommentAuthors: List<Member1>,
): Comment =
    parentComment.toData(parentCommentAuthor, childComments, childCommentAuthors)

fun CommentApiModel.toData(
    author: Member1,
    childCommentApiModels: List<CommentApiModel>,
    childCommentAuthors: List<Member1>,
) = Comment(
    id = commentId,
    author = author,
    content = content,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
    deleted = deleted,
    childComments = childCommentApiModels.zip(childCommentAuthors) { childCommentApiModel, childCommentAuthor ->
        childCommentApiModel.toData(childCommentAuthor)
    }
)

fun CommentApiModel.toData(author: Member1) = Comment(
    id = commentId,
    author = author,
    content = content,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
    deleted = deleted,
    childComments = listOf()
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")