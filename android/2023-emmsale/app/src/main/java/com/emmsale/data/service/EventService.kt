package com.emmsale.data.service

import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.apiModel.response.ConferenceResponse
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
    ): Response<List<ConferenceResponse>>

    @GET("/events")
    suspend fun getCompetitions(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<List<CompetitionResponse>>
}
