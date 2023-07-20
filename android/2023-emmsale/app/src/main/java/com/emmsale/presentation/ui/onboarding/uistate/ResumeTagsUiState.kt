package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.resumeTag.ResumeTag

sealed class ResumeTagsUiState {
    data class Success(val tags: List<ResumeTagUiState>) : ResumeTagsUiState() {
        fun insertFront(education: ResumeTagUiState): Success =
            Success(education + this.tags)
    }

    object Error : ResumeTagsUiState()

    companion object {
        fun from(tagsResult: ApiSuccess<List<ResumeTag>>): Success =
            Success(tags = ResumeTagUiState.from(tagsResult.data))
    }
}
