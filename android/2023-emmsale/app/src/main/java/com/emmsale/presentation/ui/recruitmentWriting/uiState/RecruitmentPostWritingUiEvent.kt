package com.emmsale.presentation.ui.recruitmentWriting.uiState

sealed interface RecruitmentPostWritingUiEvent {
    data class PostComplete(val recruitmentId: Long) : RecruitmentPostWritingUiEvent
    object PostFail : RecruitmentPostWritingUiEvent
    object EditComplete : RecruitmentPostWritingUiEvent
    object EditFail : RecruitmentPostWritingUiEvent
}
