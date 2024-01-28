package com.emmsale.data.apiModel.response

import com.emmsale.data.apiModel.serializer.DateSerializer
import com.emmsale.data.apiModel.serializer.DateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class RecruitmentResponse(
    @SerialName("postId")
    val id: Long,
    @SerialName("content")
    val content: String,
    @SerialName("updatedAt")
    @Serializable(with = DateSerializer::class)
    val updatedAt: LocalDate,
    @SerialName("member")
    val member: MemberResponse,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("eventName")
    val eventName: String = "",
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
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,
)
