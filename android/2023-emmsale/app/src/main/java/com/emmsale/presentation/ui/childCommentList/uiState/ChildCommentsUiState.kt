package com.emmsale.presentation.ui.childCommentList.uiState

import com.emmsale.data.model.Comment
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

data class ChildCommentsUiState(val comments: List<CommentUiState> = emptyList()) {

    fun highlight(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.highlight() else it },
    )

    fun unhighlight(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.unhighlight() else it },
    )

    companion object {
        fun create(
            uid: Long,
            parentComment: Comment,
        ) = ChildCommentsUiState(
            comments = listOf(CommentUiState.create(uid, parentComment)) +
                parentComment.childComments.map { CommentUiState.create(uid, it) },
        )
    }
}
