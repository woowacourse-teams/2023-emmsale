package com.emmsale.data.scrap

import com.emmsale.data.common.ApiResult

interface ScrappedEventRepository {
    suspend fun getScrappedEvents(): ApiResult<List<ScrappedEvent>>

    suspend fun scrapEvent(eventId: Long): ApiResult<Unit>

    suspend fun deleteScrap(eventId: Long): ApiResult<Unit>

    suspend fun isScraped(eventId: Long): ApiResult<Boolean>
}
