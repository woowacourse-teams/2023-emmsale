package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.Event
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
    ): ApiResponse<List<Event>>

    suspend fun getEventDetail(eventId: Long): ApiResponse<Event>

    suspend fun searchEvents(
        keyword: String,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        tags: List<EventTag> = emptyList(),
        statuses: List<ConferenceStatus> = emptyList(),
        category: String? = null,
    ): ApiResponse<List<Event>>

    suspend fun getScrappedEvents(): ApiResponse<List<Event>>

    suspend fun scrapEvent(eventId: Long): ApiResponse<Unit>

    suspend fun scrapOffEvent(eventId: Long): ApiResponse<Unit>

    suspend fun isScraped(eventId: Long): ApiResponse<Boolean>
}
