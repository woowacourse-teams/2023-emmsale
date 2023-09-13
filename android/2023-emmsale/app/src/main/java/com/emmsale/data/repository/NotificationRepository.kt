package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.RecruitmentNotification
import com.emmsale.data.model.RecruitmentStatus
import com.emmsale.data.model.updatedNotification.UpdatedNotification

interface NotificationRepository {
    suspend fun getRecruitmentNotifications(memberId: Long): ApiResult<List<RecruitmentNotification>>

    // TODO: 4차 스프린트에서 알림 삭제 기능 구현 예정
    // fun deleteNotification(notificationId: Long)

    suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResult<Unit>

    suspend fun updateNotificationReadStatus(
        notificationId: Long,
    ): ApiResult<Unit>

    suspend fun getUpdatedNotifications(memberId: Long): ApiResult<List<UpdatedNotification>>

    suspend fun updateUpdatedNotificationReadStatus(
        notificationId: Long,
    ): ApiResult<Unit>

    suspend fun deleteUpdatedNotifications(
        notificationIds: List<Long>,
    ): ApiResult<Unit>

    suspend fun reportRecruitmentNotification(
        recruitmentNotificationId: Long,
        senderId: Long,
        reporterId: Long,
    ): ApiResult<Unit>
}