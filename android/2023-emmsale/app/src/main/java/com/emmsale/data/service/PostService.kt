package com.emmsale.data.service

import com.emmsale.data.apiModel.response.PostsResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PostService {
    @GET("/feeds?")
    suspend fun getPosts(
        @Query("event-id") eventId: Long,
    ): ApiResponse<PostsResponse>
}
