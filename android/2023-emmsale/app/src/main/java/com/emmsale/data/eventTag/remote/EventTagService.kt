package com.emmsale.data.eventTag.remote

import com.emmsale.data.eventTag.remote.dto.EventTagApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EventTagService {
    @GET("/interest-tags")
    suspend fun getInterestEventTags(
        @Query("member_id") memberId: Long,
    ): Response<List<EventTagApiModel>>
}
