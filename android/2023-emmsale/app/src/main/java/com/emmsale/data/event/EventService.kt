package com.emmsale.data.event

import com.emmsale.data.event.dto.EventApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventService {
    @GET("/events")
    fun getEvents(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("status") status: String,
        @Query("tags") tags: List<String>
    ): Response<List<EventApiModel>>
}
