package com.emmsale.data.scrap

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.scrap.dto.ScrappedEventApiModel
import com.emmsale.data.scrap.dto.ScrappedEventRequestBody
import com.emmsale.data.scrap.mapper.toData
import okhttp3.Headers

class ScrappedEventRepositoryImpl(
    private val scrappedEventService: ScrappedEventService,
) : ScrappedEventRepository {

    override suspend fun getScrappedEvents(): ApiResult<List<ScrappedEvent>> {
        return handleApi(
            execute = { scrappedEventService.getScrappedEvents() },
            mapToDomain = List<ScrappedEventApiModel>::toData,
        )
    }

    override suspend fun scrapEvent(eventId: Long): ApiResult<Unit> {
        val scrappedEventRequestBody = ScrappedEventRequestBody(eventId)
        return handleApi(
            execute = { scrappedEventService.scrapEvent(scrappedEventRequestBody) },
            mapToDomain = {},
        )
    }

    override suspend fun deleteScrap(eventId: Long): ApiResult<Unit> {
        return handleApi(
            execute = { scrappedEventService.deleteScrap(eventId) },
            mapToDomain = {},
        )
    }

    override suspend fun isScraped(eventId: Long): ApiResult<Boolean> {
        return when (val response = getScrappedEvents()) {
            is ApiError -> ApiError(response.code, response.message)
            is ApiException -> ApiException(response.e)
            is ApiSuccess -> ApiSuccess(
                response.data.any { it.eventId == eventId },
                DEFAULT_HEADER,
            )
        }
    }

    companion object {
        private val DEFAULT_HEADER = Headers.headersOf("Auth", "Auth")
    }
}
