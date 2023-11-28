package com.emmsale.data.service

import com.emmsale.data.apiModel.request.ScrappedEventCreateRequest
import com.emmsale.data.apiModel.response.EventResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
    ): ApiResponse<List<EventResponse>>

    @GET("/events")
    suspend fun getCompetitions(
        @Query("category") category: String,
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("tags") tags: List<String> = emptyList(),
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): ApiResponse<List<EventResponse>>

    @GET("/events/{eventId}")
    suspend fun getEventDetail(
        @Path("eventId") eventId: Long,
    ): ApiResponse<EventResponse>

    @GET("/events")
    suspend fun searchEvents(
        @Query("keyword") keyword: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("tags") tags: List<String> = emptyList(),
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("category") category: String? = null,
    ): ApiResponse<List<EventResponse>>

    @GET("/scraps")
    suspend fun getScrappedEvents(): ApiResponse<List<EventResponse>>

    @POST("/scraps")
    suspend fun scrapEvent(
        @Body scrappedEventCreateRequest: ScrappedEventCreateRequest,
    ): ApiResponse<Unit>

    @DELETE("/scraps")
    suspend fun deleteScrap(
        @Query("event-id") eventId: Long,
    ): ApiResponse<Unit>
}
