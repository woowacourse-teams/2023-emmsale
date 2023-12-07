package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.notification.Notification

interface NotificationRepository {

    suspend fun getNotifications(memberId: Long): ApiResponse<List<Notification>>

    suspend fun readNotification(notificationId: Long): ApiResponse<Unit>

    suspend fun deleteNotifications(notificationIds: List<Long>): ApiResponse<Unit>
}
