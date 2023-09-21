package com.emmsale.data.service

import com.emmsale.data.apiModel.response.PostsResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface PostService {
    @GET("/feeds?")
    suspend fun getPosts(
        @Query("event-id") eventId: Long,
    ): ApiResponse<PostsResponse>

    @Multipart // <- 이 부분이 중요
    @POST("/feeds")
    suspend fun uploadPost(
        @PartMap feedPostRequest: HashMap<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>,
    ): ApiResponse<Long>
}
