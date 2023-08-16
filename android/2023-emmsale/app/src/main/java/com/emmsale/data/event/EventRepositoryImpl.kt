package com.emmsale.data.event

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.competitionStatus.CompetitionStatus
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.event.dto.CompetitionApiModel
import com.emmsale.data.event.dto.ConferenceApiModel
import com.emmsale.data.event.mapper.toApiModel
import com.emmsale.data.event.mapper.toData
import com.emmsale.data.event.model.Competition
import com.emmsale.data.event.model.Conference
import com.emmsale.data.eventTag.EventTag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getConferences(
        statuses: List<ConferenceStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResult<List<Conference>> = withContext(dispatcher) {
        handleApi(
            execute = {
                eventService.getConferences(
                    category = EventCategory.CONFERENCE.toApiModel(),
                    statuses = statuses.toApiModel(),
                    tags = tags.map { it.name },
                    startDate = startDate?.toRequestFormat(),
                    endDate = endDate?.toRequestFormat(),
                )
            },
            mapToDomain = List<ConferenceApiModel>::toData,
        )
    }

    override suspend fun getCompetitions(
        statuses: List<CompetitionStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResult<List<Competition>> = withContext(dispatcher) {
        handleApi(
            execute = {
                eventService.getCompetitions(
                    category = EventCategory.COMPETITION.toApiModel(),
                    statuses = statuses.toApiModel(),
                    tags = tags.map { it.name },
                    startDate = startDate?.toRequestFormat(),
                    endDate = endDate?.toRequestFormat(),
                )
            },
            mapToDomain = List<CompetitionApiModel>::toData,
        )
    }

    private fun EventCategory.toApiModel(): String = when (this) {
        EventCategory.CONFERENCE -> "CONFERENCE"
        EventCategory.COMPETITION -> "COMPETITION"
    }

    private fun LocalDate?.toRequestFormat(): String? {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(this)
    }
}
