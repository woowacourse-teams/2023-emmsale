package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.conference.dto.ConferenceApiModel
import com.emmsale.data.conference.mapper.toApiModel
import com.emmsale.data.conference.mapper.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConferenceRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val conferenceService: ConferenceService,
) : ConferenceRepository {
    override suspend fun getConferences(
        category: EventCategory,
        startDate: String?,
        endDate: String?,
        statuses: List<ConferenceStatus>,
        tags: List<String>,
    ): ApiResult<List<Conference>> = withContext(dispatcher) {
        handleApi(
            execute = {
                conferenceService.getEvents(
                    category = category.text,
                    startDate = startDate,
                    endDate = endDate,
                    statuses = statuses.toApiModel(),
                    tags = tags,
                )
            },
            mapToDomain = List<ConferenceApiModel>::toData,
        )
    }
}
