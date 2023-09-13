package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequestBody(
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
        fun createCommentReport(commentId: Long, authorId: Long, reporterId: Long) =
            ReportRequestBody(
                reporterId = reporterId,
                reportedId = authorId,
                type = "COMMENT",
                contentId = commentId,
            )

        fun createRecruitmentReport(recruitmentId: Long, authorId: Long, reporterId: Long) =
            ReportRequestBody(
                reporterId = reporterId,
                reportedId = authorId,
                type = "PARTICIPANT",
                contentId = recruitmentId,
            )

        fun createRecruitmentNotificationReport(
            recruitmentNotificationId: Long,
            senderId: Long,
            reporterId: Long,
        ) = ReportRequestBody(
            reporterId = reporterId,
            reportedId = senderId,
            type = "REQUEST_NOTIFICATION",
            contentId = recruitmentNotificationId,
        )
    }
}
