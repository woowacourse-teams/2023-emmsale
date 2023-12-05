package com.emmsale.presentation.ui.recruitmentDetail.uiState

sealed interface RecruitmentPostDetailUiEvent {
    object PostFetchFail : RecruitmentPostDetailUiEvent
    object PostDeleteComplete : RecruitmentPostDetailUiEvent
    object PostDeleteFail : RecruitmentPostDetailUiEvent
    object ReportFail : RecruitmentPostDetailUiEvent
    object ReportComplete : RecruitmentPostDetailUiEvent
    object ReportDuplicate : RecruitmentPostDetailUiEvent
    data class MessageSendComplete(val roomId: String, val otherId: Long) :
        RecruitmentPostDetailUiEvent

    object MessageSendFail : RecruitmentPostDetailUiEvent
}
