package com.emmsale.data.service

import com.emmsale.data.apiModel.response.EventDetailApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EventDetailService {
    @GET("/events/{eventId}")
    suspend fun getEventDetail(
        @Path("eventId") eventId: Long,
    ): ApiResponse<EventDetailApiModel>
}
