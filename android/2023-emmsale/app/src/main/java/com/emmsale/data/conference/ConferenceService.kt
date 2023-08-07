package com.emmsale.data.conference

import com.emmsale.data.conference.dto.ConferenceApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ConferenceService {
    @GET("/events")
    suspend fun getEvents(
        @Query("category") category: String,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
    ): Response<List<ConferenceApiModel>>
}
