package com.emmsale.data.network.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentNotificationStatusUpdateRequest(
    @SerialName("updatedStatus")
    val updatedStatus: String,
)

@Serializable
data class NotificationListDeleteRequest(
    @SerialName("deleteIds")
    val notificationIds: List<Long>,
)

@Serializable
data class RecruitmentNotificationReportCreateRequest(
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
        private const val REPORT_TYPE = "REQUEST_NOTIFICATION"

        fun create(
            recruitmentNotificationId: Long,
            senderId: Long,
            reporterId: Long,
        ): RecruitmentNotificationReportCreateRequest = RecruitmentNotificationReportCreateRequest(
            reporterId = reporterId,
            reportedId = senderId,
            type = REPORT_TYPE,
            contentId = recruitmentNotificationId,
        )
    }
}
