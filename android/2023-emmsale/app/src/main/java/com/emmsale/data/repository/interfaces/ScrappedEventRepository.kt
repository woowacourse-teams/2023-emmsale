package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.ScrappedEvent

interface ScrappedEventRepository {
    suspend fun getScrappedEvents(): ApiResult<List<ScrappedEvent>>

    suspend fun scrapEvent(eventId: Long): ApiResult<Unit>

    suspend fun deleteScrap(eventId: Long): ApiResult<Unit>

    suspend fun isScraped(eventId: Long): ApiResult<Boolean>
}
