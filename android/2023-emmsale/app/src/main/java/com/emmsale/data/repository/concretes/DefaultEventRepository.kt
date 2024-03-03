package com.emmsale.data.repository.concretes

import com.emmsale.data.mapper.toApiModel
import com.emmsale.data.mapper.toData
import com.emmsale.data.network.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.network.apiModel.response.EventResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.network.callAdapter.Failure
import com.emmsale.data.network.callAdapter.NetworkError
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.network.callAdapter.Unexpected
import com.emmsale.data.network.di.IoDispatcher
import com.emmsale.data.network.service.EventService
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.model.CompetitionStatus
import com.emmsale.model.ConferenceStatus
import com.emmsale.model.Event
import com.emmsale.model.EventCategory
import com.emmsale.model.EventTag
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
    ): ApiResponse<List<Event>> = withContext(dispatcher) {
        eventService.getConferences(
            category = EventCategory.CONFERENCE.toApiModel(),
            statuses = statuses.toApiModel(),
            tags = tags.map { it.name },
            startDate = startDate?.toRequestFormat(),
            endDate = endDate?.toRequestFormat(),
        ).map { it.toData() }
    }

    override suspend fun getCompetitions(
        statuses: List<CompetitionStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResponse<List<Event>> = withContext(dispatcher) {
        eventService.getCompetitions(
            category = EventCategory.COMPETITION.toApiModel(),
            statuses = statuses.toApiModel(),
            tags = tags.map { it.name },
            startDate = startDate?.toRequestFormat(),
            endDate = endDate?.toRequestFormat(),
        ).map { it.toData() }
    }

    override suspend fun getEventDetail(eventId: Long): ApiResponse<Event> {
        return eventService
            .getEventDetail(eventId)
            .map(EventResponse::toData)
    }

    override suspend fun searchEvents(
        keyword: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        tags: List<EventTag>,
        statuses: List<ConferenceStatus>,
        category: String?,
    ): ApiResponse<List<Event>> = withContext(dispatcher) {
        eventService.searchEvents(
            keyword = keyword,
            startDate = startDate?.toRequestFormat(),
            endDate = endDate?.toRequestFormat(),
            tags = tags.map { it.name },
            statuses = statuses.toApiModel(),
            category = category,
        ).map { it.toData() }
    }

    private fun EventCategory.toApiModel(): String = when (this) {
        EventCategory.CONFERENCE -> "CONFERENCE"
        EventCategory.COMPETITION -> "COMPETITION"
    }

    private fun LocalDate?.toRequestFormat(): String? {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(this)
    }

    override suspend fun getScrappedEvents(): ApiResponse<List<Event>> {
        return eventService
            .getScrappedEvents()
            .map(List<EventResponse>::toData)
    }

    override suspend fun scrapEvent(eventId: Long): ApiResponse<Unit> {
        return eventService.scrapEvent(
            ScrappedEventCreateRequest(eventId),
        )
    }

    override suspend fun scrapOffEvent(eventId: Long): ApiResponse<Unit> {
        return eventService.deleteScrap(eventId)
    }

    override suspend fun isScraped(eventId: Long): ApiResponse<Boolean> {
        return when (val response = getScrappedEvents()) {
            is Failure -> response
            is NetworkError -> response
            is Unexpected -> response
            is Success -> Success(
                response.data.any { it.id == eventId },
            )
        }
    }
}
