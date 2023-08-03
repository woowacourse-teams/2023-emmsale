package com.emmsale.data.eventTag

import com.emmsale.data.eventTag.dto.ConferenceTagApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventTagService {
    @GET("/event/tag")
    suspend fun getConferenceTags(
        @Query("category") category: String,
    ): Response<List<ConferenceTagApiModel>>
}
