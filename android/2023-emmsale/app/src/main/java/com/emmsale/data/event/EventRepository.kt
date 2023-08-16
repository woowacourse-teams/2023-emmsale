package com.emmsale.data.event

import com.emmsale.data.common.ApiResult
import com.emmsale.data.competitionStatus.CompetitionStatus
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.event.model.Competition
import com.emmsale.data.event.model.Conference
import com.emmsale.data.eventTag.EventTag
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
