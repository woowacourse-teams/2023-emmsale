package com.emmsale.data.myPost

import com.emmsale.data.myPost.dto.MyPostApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPostService {
    @GET("events/recruitment-posts")
    suspend fun getMyPosts(
        @Query("member-id") memberId: Long,
    ): Response<List<MyPostApiModel>>
}
