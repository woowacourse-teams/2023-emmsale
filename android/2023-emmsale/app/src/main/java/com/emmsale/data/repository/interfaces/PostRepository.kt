package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Post

interface PostRepository {
    suspend fun getPosts(eventId: Long): ApiResponse<List<Post>>
}
