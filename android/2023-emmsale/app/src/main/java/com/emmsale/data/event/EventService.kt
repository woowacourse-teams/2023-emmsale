package com.emmsale.data.event

import com.emmsale.data.event.dto.ConferenceApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventService {
    @GET("/events/conferences")
    suspend fun getEvents(
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("status") status: String? = null,
        @Query("tag") tag: String? = null,
    ): Response<List<ConferenceApiModel>>
}
