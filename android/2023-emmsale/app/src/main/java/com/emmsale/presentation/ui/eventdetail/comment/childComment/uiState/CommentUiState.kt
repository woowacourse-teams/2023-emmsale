package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.model.Comment
import java.time.format.DateTimeFormatter

data class CommentUiState(
    val authorId: Long,
    val authorName: String,
    val authorImageUrl: String,
    val lastModifiedDate: String,
    val isUpdated: Boolean,
    val id: Long,
    val content: String,
    val isUpdatable: Boolean,
    val isDeletable: Boolean,
    val isReportable: Boolean,
    val isDeleted: Boolean,
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        fun create(
            comment: Comment,
            loginMemberId: Long,
        ) = CommentUiState(
            authorId = comment.authorId,
            authorName = comment.authorName,
            authorImageUrl = comment.authorImageUrl,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            id = comment.id,
            content = comment.content,
            isUpdatable = comment.authorId == loginMemberId,
            isDeletable = comment.authorId == loginMemberId,
            isReportable = comment.authorId != loginMemberId,
            isDeleted = comment.deleted,
        )
    }
}
