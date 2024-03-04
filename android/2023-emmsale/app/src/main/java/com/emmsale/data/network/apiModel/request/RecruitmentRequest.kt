package com.emmsale.data.network.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentCreateRequest(
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("content")
    val content: String,
)

@Serializable
data class RecruitmentDeleteRequest(
    @SerialName("content")
    val content: String,
)

@Serializable
data class RecruitmentRequestCreateRequest(
    @SerialName("senderId")
    val senderId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("message")
    val message: String,
    @SerialName("eventId")
    val eventId: Long,
)

@Serializable
data class RecruitmentReportCreateRequest(
    @SerialName("reporterId")
    val reporterId: Long,
    @SerialName("reportedId")
    val reportedId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("contentId")
    val contentId: Long,
) {
    companion object {
        private const val REPORT_TYPE = "PARTICIPANT"

        fun create(
            recruitmentId: Long,
            authorId: Long,
            reporterId: Long,
        ): RecruitmentReportCreateRequest = RecruitmentReportCreateRequest(
            reporterId = reporterId,
            reportedId = authorId,
            type = REPORT_TYPE,
            contentId = recruitmentId,
        )
    }
}
