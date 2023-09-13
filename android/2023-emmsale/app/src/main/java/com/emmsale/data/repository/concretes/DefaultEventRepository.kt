package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.apiModel.response.ConferenceResponse
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toApiModel
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.Conference
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.EventCategory
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.service.EventService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DefaultEventRepository(
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
            mapToDomain = List<ConferenceResponse>::toData,
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
            mapToDomain = List<CompetitionResponse>::toData,
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
