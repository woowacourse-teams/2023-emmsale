package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult
import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.notification.updated.UpdatedNotification

interface NotificationRepository {
    suspend fun getRecruitmentNotifications(): ApiResult<List<RecruitmentNotification>>

    // TODO: 4차 스프린트에서 알림 삭제 기능 구현 예정
    // fun deleteNotification(notificationId: Long)

    suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResult<Unit>

    suspend fun updateNotificationReadStatus(
        notificationId: Long,
        isRead: Boolean,
    ): ApiResult<Unit>

    suspend fun getUpdatedNotifications(memberId: Long): ApiResult<List<UpdatedNotification>>
}
