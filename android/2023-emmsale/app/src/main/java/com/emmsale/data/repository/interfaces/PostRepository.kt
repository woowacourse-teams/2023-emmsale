package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.Post
import java.io.File

interface PostRepository {
    suspend fun getPosts(eventId: Long): ApiResponse<List<Post>>

    suspend fun uploadPost(
        eventId: Long,
        title: String,
        content: String,
        imageFiles: List<File>,
    ): ApiResponse<Long>
}
