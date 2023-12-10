package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Comment
import com.emmsale.data.model.Feed

data class FeedDetailUiState(
    val feed: FeedUiState = FeedUiState(),
    val comments: CommentsUiState = CommentsUiState(),
) {
    constructor(feed: Feed, comments: List<Comment>, uid: Long) : this(
        feed = FeedUiState(feed),
        comments = CommentsUiState(uid, comments),
    )
}
