package com.emmsale.data.conference

import com.emmsale.data.conference.dto.ConferenceApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ConferenceService {
    @GET("/events")
    suspend fun getEvents(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<List<ConferenceApiModel>>
}
