package com.emmsale.data.service

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.apiModel.response.FeedResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {

    @GET("/feeds?")
    suspend fun getFeeds(
        @Query("event-id") eventId: Long,
    ): ApiResponse<List<FeedResponse>>

    @GET("/feeds/{feedId}")
    suspend fun getFeed(
        @Path("feedId") feedId: Long,
    ): ApiResponse<FeedDetailResponse>

    @GET("/feeds/{feedId}")
    suspend fun getFeed2(
        @Path("feedId") feedId: Long,
    ): ApiResponse<FeedResponse>

    @DELETE("/feeds/{feedId}")
    suspend fun deleteFeed(
        @Path("feedId") feedId: Long,
    ): ApiResponse<Unit>

    @Multipart // <- 이 부분이 중요
    @POST("/feeds")
    suspend fun uploadFeed(
        @PartMap feedPostRequest: HashMap<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>,
    ): ApiResponse<Long>
}
