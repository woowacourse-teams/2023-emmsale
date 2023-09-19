package com.emmsale.presentation.ui.feedDetail.uiState

sealed interface FeedDetailUiEvent {
    object None : FeedDetailUiEvent
    data class UnexpectedError(val errorMessage: String) : FeedDetailUiEvent
    object CommentPostFail : FeedDetailUiEvent
    object CommentUpdateFail : FeedDetailUiEvent
    object CommentDeleteFail : FeedDetailUiEvent
    object CommentReportDuplicate : FeedDetailUiEvent
    object CommentReportFail : FeedDetailUiEvent
    object CommentReportComplete : FeedDetailUiEvent
}
