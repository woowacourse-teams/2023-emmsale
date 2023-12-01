package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.updatedNotification.UpdatedNotification

interface NotificationRepository {

    suspend fun updateNotificationReadStatus(notificationId: Long): ApiResponse<Unit>

    suspend fun getUpdatedNotifications(memberId: Long): ApiResponse<List<UpdatedNotification>>

    suspend fun updateUpdatedNotificationReadStatus(notificationId: Long): ApiResponse<Unit>

    suspend fun deleteUpdatedNotifications(notificationIds: List<Long>): ApiResponse<Unit>

    suspend fun reportRecruitmentNotification(
        recruitmentNotificationId: Long,
        senderId: Long,
        reporterId: Long,
    ): ApiResponse<Unit>
}
