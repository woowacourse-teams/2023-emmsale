package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.GeneralPost

interface PostRepository {
    suspend fun getPosts(eventId: Long): ApiResponse<List<GeneralPost>>

    suspend fun uploadPost(
        eventId: Long,
        title: String,
        content: String,
        imageUrls: List<String>,
    ): ApiResponse<Long>
}
