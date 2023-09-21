package com.emmsale.data.repository.interfaces

import com.emmsale.data.model.CompetitionStatus

interface CompetitionStatusRepository {

    suspend fun getCompetitionStatuses(): List<CompetitionStatus>

    suspend fun getCompetitionStatusByIds(tagIds: Array<Long>): List<CompetitionStatus>
}