package com.emmsale.data.competitionStatus

interface CompetitionStatusRepository {
    suspend fun getCompetitionStatuses(): List<CompetitionStatus>
    suspend fun getCompetitionStatusByIds(ids: Array<Long>): List<CompetitionStatus>
}
