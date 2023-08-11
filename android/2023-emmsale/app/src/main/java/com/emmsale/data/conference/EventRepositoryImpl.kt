package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.conference.dto.ConferenceApiModel
import com.emmsale.data.conference.mapper.toApiModel
import com.emmsale.data.conference.mapper.toData
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.eventTag.EventTag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val conferenceService: ConferenceService,
) : EventRepository {
    override suspend fun getEvents(
        category: EventCategory,
        statuses: List<ConferenceStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResult<List<Conference>> = withContext(dispatcher) {
        handleApi(
            execute = {
                conferenceService.getEvents(
                    category = category.text,
                    statuses = statuses.toApiModel(),
                    tags = tags.map { it.name },
                    startDate = startDate?.toRequestFormat(),
                    endDate = endDate?.toRequestFormat(),
                )
            },
            mapToDomain = List<ConferenceApiModel>::toData,
        )
    }

    private fun LocalDate?.toRequestFormat(): String? {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(this)
    }
}
