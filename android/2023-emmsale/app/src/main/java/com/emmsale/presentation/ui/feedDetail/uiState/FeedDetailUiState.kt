package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.model.Comment
import com.emmsale.model.Feed

data class FeedDetailUiState(
    val feedUiState: FeedUiState = FeedUiState(),
    val commentsUiState: CommentsUiState = CommentsUiState(),
) {
    constructor(feed: Feed, comments: List<Comment>, uid: Long) : this(
        feedUiState = FeedUiState(feed),
        commentsUiState = CommentsUiState(uid, comments),
    )

    fun getCommentPosition(commentId: Long): Int? {
        val commentPosition = commentsUiState.getPosition(commentId) ?: return null
        return commentPosition + 1 // 게시글까지 계산해서 1을 더함
    }

    fun highlightComment(commentId: Long): FeedDetailUiState =
        copy(commentsUiState = commentsUiState.highlight(commentId))

    fun unhighlightComment(): FeedDetailUiState =
        copy(commentsUiState = commentsUiState.unhighlight())
}
