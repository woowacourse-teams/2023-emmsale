package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.resumeTag.ResumeTag

data class ResumeTagUiState(
    val name: String,
) {
    operator fun plus(tags: List<ResumeTagUiState>): List<ResumeTagUiState> =
        listOf(this) + tags

    companion object {
        fun from(tags: List<ResumeTag>): List<ResumeTagUiState> =
            tags.map { from(it) }

        private fun from(tag: ResumeTag): ResumeTagUiState =
            ResumeTagUiState(name = tag.name)

        fun empty(): ResumeTagUiState = ResumeTagUiState(name = "-")
    }
}
