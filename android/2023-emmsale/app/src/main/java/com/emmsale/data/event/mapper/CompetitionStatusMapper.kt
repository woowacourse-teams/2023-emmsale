package com.emmsale.data.event.mapper

import com.emmsale.data.competitionStatus.CompetitionStatus

fun List<CompetitionStatus>.toApiModel(): List<String> = map { it.toApiModel() }

private fun CompetitionStatus.toApiModel(): String = when (this) {
    CompetitionStatus.IN_PROGRESS -> "IN_PROGRESS"
    CompetitionStatus.SCHEDULED -> "UPCOMING"
    CompetitionStatus.ENDED -> "ENDED"
}
