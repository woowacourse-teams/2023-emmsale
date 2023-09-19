package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.ScrappedEvent

interface ScrappedEventRepository {

    suspend fun getScrappedEvents(): ApiResponse<List<ScrappedEvent>>

    suspend fun scrapEvent(eventId: Long): ApiResponse<Unit>

    suspend fun deleteScrap(eventId: Long): ApiResponse<Unit>

    suspend fun isScraped(eventId: Long): ApiResponse<Boolean>
}
