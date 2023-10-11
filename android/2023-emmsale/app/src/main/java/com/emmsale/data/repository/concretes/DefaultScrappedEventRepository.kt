package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.ScrappedEvent
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.data.service.ScrappedEventService
import javax.inject.Inject

class DefaultScrappedEventRepository @Inject constructor(
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
