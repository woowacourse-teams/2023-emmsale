package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment
import com.emmsale.data.model.Feed

data class FeedDetailUiState(
    val feedUiState: FeedUiState = FeedUiState(),
    val commentsUiState: CommentsUiState = CommentsUiState(),
) {
    constructor(feed: Feed, comments: List<Comment>, uid: Long) : this(
        feedUiState = FeedUiState(feed),
        commentsUiState = CommentsUiState(uid, comments),
    )

    fun getCommentPosition(commentId: Long): Int = commentsUiState.getPosition(commentId) + 1

    fun highlightComment(commentId: Long): FeedDetailUiState =
        copy(commentsUiState = commentsUiState.highlight(commentId))

    fun unhighlightComment(): FeedDetailUiState =
        copy(commentsUiState = commentsUiState.unhighlight())
}
