package com.emmsale.data.eventdetail

import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventDetailService {
    @GET("events/{eventId}")
    suspend fun getEventDetail(
        @Path("eventId") eventId: Long,
    ): Response<EventDetailApiModel>
}
