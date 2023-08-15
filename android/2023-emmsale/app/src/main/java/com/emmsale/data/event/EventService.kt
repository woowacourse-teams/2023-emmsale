package com.emmsale.data.event

import com.emmsale.data.event.dto.CompetitionApiModel
import com.emmsale.data.event.dto.ConferenceApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventService {
    @GET("/events")
    suspend fun getConferences(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<List<ConferenceApiModel>>

    @GET("/events")
    suspend fun getCompetitions(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<List<CompetitionApiModel>>
}
