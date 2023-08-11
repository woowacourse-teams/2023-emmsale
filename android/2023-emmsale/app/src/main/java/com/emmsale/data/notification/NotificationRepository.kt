package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult

interface NotificationRepository {
    suspend fun getRecruitmentNotifications(): ApiResult<List<RecruitmentNotification>>

    // TODO: 4차 스프린트에서 알림 삭제 기능 구현 예정
    // fun deleteNotification(notificationId: Long)

    suspend fun updateRecruitmentAcceptedStatus(
        notificationId: Long,
        isAccepted: Boolean,
    ): ApiResult<Unit>

    suspend fun updateNotificationReadStatus(
        notificationId: Long,
        isRead: Boolean,
    ): ApiResult<Unit>
}
