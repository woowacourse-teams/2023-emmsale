package com.emmsale.presentation.ui.eventDetail.uiState

sealed interface EventDetailUiEvent {
    object ScrapFail : EventDetailUiEvent
    object ScrapOffFail : EventDetailUiEvent
    object RecruitmentPostApproval : EventDetailUiEvent
    object RecruitmentIsAlreadyPosted : EventDetailUiEvent
    object RecruitmentPostedCheckFail : EventDetailUiEvent
}
