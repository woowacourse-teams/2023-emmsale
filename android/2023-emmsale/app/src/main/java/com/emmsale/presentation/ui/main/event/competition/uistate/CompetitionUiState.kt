package com.emmsale.presentation.ui.main.event.competition.uistate

import com.emmsale.data.competitionStatus.CompetitionStatus
import com.emmsale.data.event.model.Competition

data class CompetitionUiState(
    val id: Long,
    val name: String,
    val tags: List<String>,
    val status: String,
    val posterUrl: String?,
) {
    companion object {
        fun from(competition: Competition): CompetitionUiState = CompetitionUiState(
            id = competition.id,
            name = competition.name,
            tags = competition.tags,
            status = getStatus(competition),
            posterUrl = competition.posterUrl,
        )

        private fun getStatus(competition: Competition): String = when (competition.status) {
            CompetitionStatus.IN_PROGRESS -> "진행중"
            CompetitionStatus.SCHEDULED -> "D-${competition.dDay}"
            CompetitionStatus.ENDED -> "마감"
        }
    }
}
