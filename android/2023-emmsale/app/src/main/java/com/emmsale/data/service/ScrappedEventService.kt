package com.emmsale.data.service

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.ScrappedEventApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ScrappedEventService {
    @GET("scraps")
    suspend fun getScrappedEvents(): Response<List<ScrappedEventApiModel>>

    @POST("scraps")
    suspend fun scrapEvent(
        @Body scrappedEventCreateRequest: ScrappedEventCreateRequest,
    ): Response<Unit>

    @DELETE("scraps")
    suspend fun deleteScrap(
        @Query("event-id") eventId: Long,
    ): Response<Unit>
}
