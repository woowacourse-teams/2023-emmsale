package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName

data class PostResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("writerId")
    val writerId: Long,
    @SerialName("commentsCount")
    val commentsCount: Int = 0,
    @SerialName("content")
    val content: String = "",
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("createdAt")
    val createdAt: String,
)

data class PostsResponse(
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("feeds")
    val posts: List<PostResponse> = emptyList(),
)
