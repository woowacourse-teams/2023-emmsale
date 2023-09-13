package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.Conference
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.EventTag
import java.time.LocalDate

interface EventRepository {
    suspend fun getConferences(
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): ApiResult<List<Conference>>

    suspend fun getCompetitions(
        statuses: List<CompetitionStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): ApiResult<List<Competition>>
}