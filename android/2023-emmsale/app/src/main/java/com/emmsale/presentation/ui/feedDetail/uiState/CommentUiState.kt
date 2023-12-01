package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment

data class CommentUiState(
    val isWrittenByLoginUser: Boolean,
    val isHighlight: Boolean,
    val comment: Comment,
) {
    val isUpdated: Boolean = comment.createdAt != comment.updatedAt

    val childCommentsCount = comment.childComments.count { !it.isDeleted }

    fun highlight() = copy(isHighlight = true)

    fun unhighlight() = copy(isHighlight = false)

    companion object {
        fun create(uid: Long, comment: Comment, isHighlight: Boolean = false): CommentUiState =
            CommentUiState(
                isWrittenByLoginUser = uid == comment.writer.id,
                isHighlight = isHighlight,
                comment = comment,
            )
    }
}
