package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentUpdateRequest(
    @SerialName("content")
    val content: String,
)

@Serializable
data class ChildCommentCreateRequest(
    @SerialName("content")
    val content: String,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("parentId")
    val parentId: Long? = null,
)

@Serializable
data class CommentReportRequest(
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
        private const val REPORT_TYPE = "COMMENT"

        fun createCommentReport(
            commentId: Long,
            authorId: Long,
            reporterId: Long,
        ): CommentReportRequest = CommentReportRequest(
            reporterId = reporterId,
            reportedId = authorId,
            type = REPORT_TYPE,
            contentId = commentId,
        )
    }
}
