package com.emmsale.data.eventTag

import com.emmsale.data.eventTag.dto.EventTagApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventTagService {
    @GET("/event/tag")
    suspend fun getEventTags(
        @Query("category") eventCategory: String,
    ): Response<List<EventTagApiModel>>
}
