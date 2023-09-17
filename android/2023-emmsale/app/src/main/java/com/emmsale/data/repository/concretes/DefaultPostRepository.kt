package com.emmsale.data.repository.concretes

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.model.Post
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.data.service.PostService
import java.time.LocalDateTime

class DefaultPostRepository(
    private val postService: PostService,
) : PostRepository {
    override suspend fun getPosts(): ApiResponse<List<Post>> {
        return Success(
            List(15) {
                Post(
                    id = 1,
                    eventId = 4629,
                    title = "타임라인이 어떻게 되나요?",
                    content = "행사 시간 공유 좀 해주실 분 부탁드려요요요ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ",
                    titleImageUrl = "https://www.google.com/#q=aptent",
                    likeCount = 5,
                    createdAt = LocalDateTime.of(2023, 5, 8, 23, 23),
                    commentCount = 10,
                )
            },
        )
    }
}
