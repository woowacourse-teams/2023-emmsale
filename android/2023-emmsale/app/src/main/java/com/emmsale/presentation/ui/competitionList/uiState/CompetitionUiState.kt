package com.emmsale.presentation.ui.competitionList.uiState

import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus

data class CompetitionUiState(
    val id: Long,
    val name: String,
    val tags: List<String>,
    val status: String,
    val posterUrl: String?,
    val isFree: Boolean,
    val isOnline: Boolean,
) {
    companion object {
        fun from(competition: Competition): CompetitionUiState = CompetitionUiState(
            id = competition.id,
            name = competition.name,
            tags = competition.tags,
            status = getStatus(competition),
            posterUrl = competition.posterUrl,
            isOnline = competition.isOnline,
            isFree = competition.isFree,
        )

        private fun getStatus(competition: Competition): String = when (competition.status) {
            CompetitionStatus.IN_PROGRESS -> "진행중"
            CompetitionStatus.SCHEDULED -> "D-${competition.dDay}"
            CompetitionStatus.ENDED -> "마감"
        }
    }
}
