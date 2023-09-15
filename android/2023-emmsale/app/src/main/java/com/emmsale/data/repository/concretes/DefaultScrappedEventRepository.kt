package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.ScrappedEvent
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.data.service.ScrappedEventService

class DefaultScrappedEventRepository(
    private val scrappedEventService: ScrappedEventService,
) : ScrappedEventRepository {

    override suspend fun getScrappedEvents(): ApiResponse<List<ScrappedEvent>> =
        scrappedEventService
            .getScrappedEvents()
            .map(List<ScrappedEventResponse>::toData)

    override suspend fun scrapEvent(
        eventId: Long,
    ): ApiResponse<Unit> = scrappedEventService.scrapEvent(ScrappedEventCreateRequest(eventId))

    override suspend fun deleteScrap(
        eventId: Long,
    ): ApiResponse<Unit> = scrappedEventService.deleteScrap(eventId)

    override suspend fun isScraped(
        eventId: Long,
    ): ApiResponse<Boolean> = getScrappedEvents()
        .map { scrappedEvents ->
            scrappedEvents.any { it.eventId == eventId }
        }
}
