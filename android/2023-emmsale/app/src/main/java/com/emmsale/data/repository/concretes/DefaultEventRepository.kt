package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.apiModel.response.ConferenceResponse
import com.emmsale.data.apiModel.response.EventDetailResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toApiModel
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.Conference
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.EventCategory
import com.emmsale.data.model.EventDetail
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.service.EventService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultEventRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getConferences(
        statuses: List<ConferenceStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResponse<List<Conference>> = withContext(dispatcher) {
        eventService.getConferences(
            category = EventCategory.CONFERENCE.toApiModel(),
            statuses = statuses.toApiModel(),
            tags = tags.map { it.name },
            startDate = startDate?.toRequestFormat(),
            endDate = endDate?.toRequestFormat(),
        ).map(List<ConferenceResponse>::toData)
    }

    override suspend fun getCompetitions(
        statuses: List<CompetitionStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResponse<List<Competition>> = withContext(dispatcher) {
        eventService.getCompetitions(
            category = EventCategory.COMPETITION.toApiModel(),
            statuses = statuses.toApiModel(),
            tags = tags.map { it.name },
            startDate = startDate?.toRequestFormat(),
            endDate = endDate?.toRequestFormat(),
        ).map(List<CompetitionResponse>::toData)
    }

    override suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail> {
        return eventService
            .getEventDetail(eventId)
            .map(EventDetailResponse::toData)
    }

    private fun EventCategory.toApiModel(): String = when (this) {
        EventCategory.CONFERENCE -> "CONFERENCE"
        EventCategory.COMPETITION -> "COMPETITION"
    }

    private fun LocalDate?.toRequestFormat(): String? {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(this)
    }
}
