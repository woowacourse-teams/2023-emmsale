package com.emmsale.data.service

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface PostService {
    @Multipart // <- 이 부분이 중요
    @POST("/feeds")
    suspend fun uploadPost(
        @PartMap feedPostRequest: HashMap<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>,
    ): ApiResponse<Long>
}
