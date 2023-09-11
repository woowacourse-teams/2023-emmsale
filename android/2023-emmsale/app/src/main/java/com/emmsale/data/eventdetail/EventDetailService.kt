package com.emmsale.data.eventdetail

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface EventDetailService {
    @GET("events/{eventId}")
    suspend fun getEventDetail(
        @Path("eventId") eventId: Long,
    ): ApiResponse<EventDetailApiModel>
}
