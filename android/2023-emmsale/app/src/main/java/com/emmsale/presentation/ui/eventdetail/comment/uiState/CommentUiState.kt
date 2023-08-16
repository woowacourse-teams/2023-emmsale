package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment
import java.time.format.DateTimeFormatter

data class CommentUiState(
    val authorId: Long,
    val authorImageUrl: String,
    val authorName: String,
    val lastModifiedDate: String,
    val isUpdated: Boolean,
    val id: Long,
    val content: String,
    val childCommentsCount: Int,
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
            authorImageUrl = comment.authorImageUrl,
            authorName = comment.authorName,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            id = comment.id,
            content = comment.content,
            childCommentsCount = comment.childComments.size,
            isUpdatable = comment.authorId == loginMemberId,
            isDeletable = comment.authorId == loginMemberId,
            isReportable = comment.authorId != loginMemberId,
            isDeleted = comment.deleted,
        )
    }
}
