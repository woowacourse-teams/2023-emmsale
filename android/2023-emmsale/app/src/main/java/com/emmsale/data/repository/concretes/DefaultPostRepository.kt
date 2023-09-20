package com.emmsale.data.repository.concretes

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Post
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.data.service.PostService

class DefaultPostRepository(
    private val postService: PostService,
) : PostRepository {

    override suspend fun getPosts(eventId: Long): ApiResponse<List<Post>> {
        return postService
            .getPosts(eventId)
            .map { it.toData() }
    }

    override suspend fun uploadPost(
        eventId: Long,
        title: String,
        content: String,
        imageUrls: List<String>,
    ): ApiResponse<Long> {
        return Success(1L)
    }
}
