package com.emmsale.data.service

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Post
import retrofit2.http.GET

interface PostService {
    @GET
    suspend fun getPosts(): ApiResponse<List<Post>>

    @GET
    suspend fun uploadPost(): ApiResponse<Long>
}
