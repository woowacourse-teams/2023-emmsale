package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentQueryRequest(
    @SerialName("feedId")
    val feedId: Long? = null,
    @SerialName("optionalMemberId")
    val memberId: Long? = null,
) {
    init {
        require(feedId != null || memberId != null) {
            "댓글 조회를 위한 객체를 생성 시 피드 아이디 또는 회원 아이디 둘 중 하나는 null이면 안됩니다."
        }
    }
}

@Serializable
data class CommentUpdateRequest(
    @SerialName("content")
    val content: String,
)

@Serializable
data class CommentCreateRequest(
    @SerialName("content")
    val content: String,
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("parentId")
    val parentId: Long? = null,
)

@Serializable
data class CommentReportCreateRequest(
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
        ): CommentReportCreateRequest = CommentReportCreateRequest(
            reporterId = reporterId,
            reportedId = authorId,
            type = REPORT_TYPE,
            contentId = commentId,
        )
    }
}
