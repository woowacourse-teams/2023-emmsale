package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.comment.Comment
import java.time.format.DateTimeFormatter

data class CommentUiState(
    val authorName: String,
    val lastModifiedDate: String,
    val isUpdated: Boolean,
    val commentId: Long,
    val content: String,
    val isUpdatable: Boolean,
    val isDeletable: Boolean,
    val isDeleted: Boolean,
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        fun create(
            comment: Comment,
            loginMemberId: Long,
        ) = CommentUiState(
            authorName = comment.authorName,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            commentId = comment.id,
            content = comment.content,
            isUpdatable = comment.authorId == loginMemberId,
            isDeletable = comment.authorId == loginMemberId,
            isDeleted = comment.deleted,
        )
    }
}
