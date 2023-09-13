package com.emmsale.data.service

import com.emmsale.data.apiModel.response.MyPostResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPostService {
    @GET("/events/recruitment-posts")
    suspend fun getMyPosts(
        @Query("member-id") memberId: Long,
    ): Response<List<MyPostResponse>>
}
