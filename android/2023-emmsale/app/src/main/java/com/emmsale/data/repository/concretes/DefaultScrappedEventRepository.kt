package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.ScrappedEvent
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.data.service.ScrappedEventService

class DefaultScrappedEventRepository(
    private val scrappedEventService: ScrappedEventService,
) : ScrappedEventRepository {

    override suspend fun getScrappedEvents(): ApiResponse<List<ScrappedEvent>> {
        return scrappedEventService
            .getScrappedEvents()
            .map(List<ScrappedEventResponse>::toData)
    }

    override suspend fun scrapEvent(eventId: Long): ApiResponse<Unit> {
        return scrappedEventService.scrapEvent(
            ScrappedEventCreateRequest(eventId),
        )
    }

    override suspend fun deleteScrap(eventId: Long): ApiResponse<Unit> {
        return scrappedEventService.deleteScrap(eventId)
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
