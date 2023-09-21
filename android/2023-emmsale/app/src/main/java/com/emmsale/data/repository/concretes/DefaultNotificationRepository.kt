package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.apiModel.request.RecruitmentNotificationReportCreateRequest
import com.emmsale.data.apiModel.response.RecruitmentNotificationResponse
import com.emmsale.data.apiModel.response.UpdatedNotificationResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.mapper.toRequestModel
import com.emmsale.data.model.RecruitmentNotification
import com.emmsale.data.model.RecruitmentStatus
import com.emmsale.data.model.updatedNotification.UpdatedNotification
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.service.NotificationService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultNotificationRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val notificationService: NotificationService,
) : NotificationRepository {
    override suspend fun getRecruitmentNotifications(
        memberId: Long,
    ): ApiResponse<List<RecruitmentNotification>> = withContext(dispatcher) {
        notificationService
            .getRecruitmentNotifications(memberId)
            .map(List<RecruitmentNotificationResponse>::toData)
    }

    override suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.updateRecruitmentStatus(
            notificationId = notificationId,
            newStatus = recruitmentStatus.toRequestModel(),
        )
    }

    override suspend fun updateNotificationReadStatus(
        notificationId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.updateNotificationReadStatus(notificationId)
    }

    override suspend fun getUpdatedNotifications(
        memberId: Long,
    ): ApiResponse<List<UpdatedNotification>> = withContext(dispatcher) {
        notificationService
            .getUpdatedNotifications(memberId)
            .map(List<UpdatedNotificationResponse>::toData)
    }

    override suspend fun updateUpdatedNotificationReadStatus(
        notificationId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.updateUpdatedNotificationReadStatus(notificationId)
    }

    override suspend fun deleteUpdatedNotifications(
        notificationIds: List<Long>,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.deleteNotification(
            NotificationListDeleteRequest(notificationIds),
        )
    }

    override suspend fun reportRecruitmentNotification(
        recruitmentNotificationId: Long,
        senderId: Long,
        reporterId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.reportRecruitmentNotification(
            RecruitmentNotificationReportCreateRequest.create(
                recruitmentNotificationId = recruitmentNotificationId,
                senderId = senderId,
                reporterId = reporterId,
            ),
        ).map { }
    }
}
