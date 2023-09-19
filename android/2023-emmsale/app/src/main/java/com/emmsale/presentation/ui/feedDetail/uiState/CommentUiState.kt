package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment

data class CommentUiState(
    val isWrittenByLoginUser: Boolean,
    val comment: Comment,
) {
    companion object {
        fun create(uid: Long, comment: Comment): CommentUiState = CommentUiState(
            isWrittenByLoginUser = uid == comment.authorId,
            comment = comment,
        )
    }
}
