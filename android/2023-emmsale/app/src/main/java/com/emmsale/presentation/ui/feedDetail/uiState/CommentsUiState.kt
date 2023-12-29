package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment

data class CommentsUiState(val commentUiStates: List<CommentUiState> = emptyList()) {

    constructor(uid: Long, comments: List<Comment>) : this(
        comments.flatMap { comment ->
            listOf(CommentUiState(uid, comment)) +
                comment.childComments.map { childComment -> CommentUiState(uid, childComment) }
        },
    )

    constructor(uid: Long, comment: Comment) : this(
        listOf(CommentUiState(uid, comment)) +
            comment.childComments.map { childComment -> CommentUiState(uid, childComment) },
    )

    val size: Int = commentUiStates.size

    operator fun get(commentId: Long): CommentUiState? =
        commentUiStates.find { it.comment.id == commentId }

    fun getPosition(commentId: Long): Int =
        commentUiStates.indexOfFirst { it.comment.id == commentId }

    fun highlight(commentId: Long) = copy(
        commentUiStates = commentUiStates.map { if (it.comment.id == commentId) it.highlight() else it.unhighlight() },
    )

    fun unhighlight() = copy(
        commentUiStates = commentUiStates.map { it.unhighlight() },
    )
}
