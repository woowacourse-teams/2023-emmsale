package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.conference.dto.ConferenceApiModel
import com.emmsale.data.conference.dto.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConferenceRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val conferenceService: ConferenceService,
) : ConferenceRepository {
    override suspend fun getConferences(
        category: EventCategory,
        year: Int?,
        month: Int?,
        statuses: List<ConferenceStatus>,
        tags: List<String>,
    ): ApiResult<List<Conference>> = withContext(dispatcher) {
        handleApi(
            conferenceService.getEvents(category.text, year, month, statuses.toTexts(), tags),
            List<ConferenceApiModel>::toData,
        )
    }
}
