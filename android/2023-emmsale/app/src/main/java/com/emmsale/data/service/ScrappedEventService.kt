package com.emmsale.data.service

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ScrappedEventService {
    @GET("/scraps")
    suspend fun getScrappedEvents(): ApiResponse<List<ScrappedEventResponse>>

    @POST("/scraps")
    suspend fun scrapEvent(
        @Body scrappedEventCreateRequest: ScrappedEventCreateRequest,
    ): ApiResponse<Unit>

    @DELETE("/scraps")
    suspend fun deleteScrap(
        @Query("event-id") eventId: Long,
    ): ApiResponse<Unit>
}
