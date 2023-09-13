package com.emmsale.data.repository

import com.emmsale.data.model.CompetitionStatus

interface CompetitionStatusRepository {
    suspend fun getCompetitionStatuses(): List<CompetitionStatus>
    suspend fun getCompetitionStatusByIds(ids: Array<Long>): List<CompetitionStatus>
}
