package com.emmsale.data.service

import com.emmsale.data.apiModel.response.CompetitionResponse
import com.emmsale.data.apiModel.response.ConferenceResponse
import com.emmsale.data.apiModel.response.EventDetailResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {
    @GET("/events")
    suspend fun getConferences(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): ApiResponse<List<ConferenceResponse>>

    @GET("/events")
    suspend fun getCompetitions(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): ApiResponse<List<CompetitionResponse>>

    @GET("/events/{eventId}")
    suspend fun getEventDetail(
        @Path("eventId") eventId: Long,
    ): ApiResponse<EventDetailResponse>

    @GET("/events")
    suspend fun searchEvents(
        @Query("keyword") keyword: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("tags") tags: List<String> = emptyList(),
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("category") category: String? = null,
    ): ApiResponse<List<ConferenceResponse>>
}
