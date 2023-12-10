package com.emmsale.presentation.ui.feedDetail.uiState

sealed interface FeedOrCommentUiState {
    val id: Long
    val viewType: Int
}
