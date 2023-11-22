package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedDetailResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("writer")
    val writer: WriterResponse,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("images")
    val imageUrls: List<String>,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)

@Serializable
data class WriterResponse(
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String,
)

@Serializable
data class FeedResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("writer")
    val writer: MemberResponse,
    @SerialName("images")
    val imageUrls: List<String>,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)
