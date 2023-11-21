package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentResponse(
    @SerialName("postId")
    val id: Long,
    @SerialName("content")
    val content: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("member")
    val member: MemberResponse,
    @SerialName("eventId")
    val eventId: Long,
)

@Serializable
data class RecruitmentReportResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("reporterId")
    val reporterId: Long,
    @SerialName("reportedId")
    val reportedId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("contentId")
    val contentId: Long,
    @SerialName("createdAt")
    val createdAt: String,
)
