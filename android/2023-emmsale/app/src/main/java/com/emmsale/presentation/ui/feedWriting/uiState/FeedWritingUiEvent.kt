package com.emmsale.presentation.ui.feedWriting.uiState

sealed interface FeedWritingUiEvent {
    data class PostComplete(val feedId: Long) : FeedWritingUiEvent
    object PostFail : FeedWritingUiEvent
}
