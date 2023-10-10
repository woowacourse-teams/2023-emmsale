package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.Event
import com.emmsale.data.model.EventDetail
import com.emmsale.data.model.EventTag
import java.time.LocalDate

interface EventRepository {

    suspend fun getConferences(
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): ApiResponse<List<Event>>

    suspend fun getCompetitions(
        statuses: List<CompetitionStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): ApiResponse<List<Competition>>

    suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail>
}
