package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Feed

data class FeedUiState(
    val feed: Feed = Feed(),
) : FeedOrCommentUiState {

    override val id: Long = feed.id

    override val viewType: Int = VIEW_TYPE

    companion object {
        const val VIEW_TYPE = 0
    }
}
