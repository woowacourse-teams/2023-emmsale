package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("writerId")
    val writerId: Long,
    @SerialName("commentCount")
    val commentCount: Int = 0,
    @SerialName("content")
    val content: String = "",
    @SerialName("images")
    val imageUrls: List<String> = emptyList(),
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)

@Serializable
data class PostsResponse(
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("feeds")
    val posts: List<PostResponse> = emptyList(),
)
