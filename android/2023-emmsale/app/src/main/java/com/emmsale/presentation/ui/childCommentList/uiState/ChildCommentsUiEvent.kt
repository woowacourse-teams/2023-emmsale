package com.emmsale.presentation.ui.childCommentList.uiState

sealed interface ChildCommentsUiEvent {
    object CommentPostFail : ChildCommentsUiEvent
    object CommentUpdateFail : ChildCommentsUiEvent
    object CommentDeleteFail : ChildCommentsUiEvent
    object CommentReportDuplicate : ChildCommentsUiEvent
    object CommentReportFail : ChildCommentsUiEvent
    object CommentReportComplete : ChildCommentsUiEvent
    object CommentPostComplete : ChildCommentsUiEvent
    object CommentUpdateComplete : ChildCommentsUiEvent
    object IllegalCommentFetch : ChildCommentsUiEvent
}
