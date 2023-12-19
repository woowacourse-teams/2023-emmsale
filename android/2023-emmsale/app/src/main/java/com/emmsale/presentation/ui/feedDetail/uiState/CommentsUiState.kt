package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment

data class CommentsUiState(val commentUiStates: List<CommentUiState> = emptyList()) {

    constructor(uid: Long, comments: List<Comment>) : this(
        comments.flatMap { comment ->
            listOf(CommentUiState.create(uid, comment)) +
                    comment.childComments.map { childComment ->
                        CommentUiState.create(uid, childComment)
                    }
        },
    )

    constructor(uid: Long, comment: Comment) : this(
        listOf(CommentUiState.create(uid, comment)) +
                comment.childComments.map { childComment ->
                    CommentUiState.create(
                        uid,
                        childComment,
                    )
                },
    )

    val size: Int = commentUiStates.size

    operator fun get(commentId: Long): CommentUiState? =
        commentUiStates.find { it.comment.id == commentId }

    fun highlight(commentId: Long) = copy(
        commentUiStates = commentUiStates.map { if (it.comment.id == commentId) it.highlight() else it.unhighlight() },
    )

    fun unhighlight() = copy(
        commentUiStates = commentUiStates.map { it.unhighlight() },
    )
}
