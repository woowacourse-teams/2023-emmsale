package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.RecruitmentNotification
import com.emmsale.data.model.RecruitmentStatus
import com.emmsale.data.model.updatedNotification.UpdatedNotification

interface NotificationRepository {

    suspend fun getRecruitmentNotifications(memberId: Long): ApiResponse<List<RecruitmentNotification>>

    suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResponse<Unit>

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
