package com.emmsale.presentation.ui.childComments.uiState

import com.emmsale.data.comment.Comment
import com.emmsale.data.member.Member1
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
            loginMember: Member1,
        ) = CommentUiState(
            authorName = comment.author.name,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            commentId = comment.id,
            content = comment.content,
            isUpdatable = comment.author == loginMember,
            isDeletable = comment.author == loginMember,
            isDeleted = comment.deleted
        )
    }
}