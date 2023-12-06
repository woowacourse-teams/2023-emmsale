package com.emmsale.presentation.ui.eventDetail.uiState

sealed interface EventDetailUiEvent {
    object ScrapFail : EventDetailUiEvent
    object ScrapOffFail : EventDetailUiEvent
}
