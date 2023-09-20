package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.PostsResponse
import com.emmsale.data.model.Post
import java.time.LocalDateTime

fun PostsResponse.toData(): List<Post> {
    return posts.map { postResponse ->
        Post(
            id = postResponse.id,
            eventId = eventId,
            title = postResponse.title,
            content = postResponse.content,
            titleImageUrl = postResponse.imageUrl,
            createdAt = LocalDateTime.parse(postResponse.createdAt),
            commentCount = postResponse.commentsCount,
        )
    }
}
