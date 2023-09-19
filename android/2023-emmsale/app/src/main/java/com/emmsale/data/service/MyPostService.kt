package com.emmsale.data.service

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPostService {
    @GET("/events/recruitment-posts")
    suspend fun getMyPosts(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<MyPostResponse>>
}
