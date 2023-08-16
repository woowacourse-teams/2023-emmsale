package com.emmsale.data.scrap

import com.emmsale.data.scrap.dto.ScrappedEventApiModel
import com.emmsale.data.scrap.dto.ScrappedEventRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScrappedEventService {
    @GET("scraps")
    suspend fun getScrappedEvents(): Response<List<ScrappedEventApiModel>>

    @POST("scraps")
    suspend fun scrapEvent(
        @Body scrappedEventRequestBody: ScrappedEventRequestBody,
    ): Response<Unit>

    @DELETE("scraps/{eventId}")
    suspend fun deleteScrap(
        @Path("eventId") eventId: Long,
    ): Response<Unit>
}
