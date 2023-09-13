package com.emmsale.data.repository

import com.emmsale.data.apiModel.request.ReportRequestBody
import com.emmsale.data.apiModel.request.UpdatedNotificationDeleteRequestModel
import com.emmsale.data.apiModel.response.RecruitmentNotificationApiModel
import com.emmsale.data.apiModel.response.UpdatedNotificationApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.mapper.toRequestModel
import com.emmsale.data.model.RecruitmentNotification
import com.emmsale.data.model.RecruitmentStatus
import com.emmsale.data.model.updatedNotification.UpdatedNotification
import com.emmsale.data.service.NotificationService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultNotificationRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val notificationService: NotificationService,
) : NotificationRepository {
    override suspend fun getRecruitmentNotifications(memberId: Long): ApiResult<List<RecruitmentNotification>> =
        withContext(dispatcher) {
            handleApi(
                execute = { notificationService.getRecruitmentNotifications(memberId) },
                mapToDomain = List<RecruitmentNotificationApiModel>::toData,
            )
        }

    override suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                notificationService.updateRecruitmentStatus(
                    notificationId = notificationId,
                    newStatus = recruitmentStatus.toRequestModel(),
                )
            },
            mapToDomain = { },
        )
    }

    override suspend fun updateNotificationReadStatus(
        notificationId: Long,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { notificationService.updateNotificationReadStatus(notificationId) },
            mapToDomain = { },
        )
    }

    override suspend fun getUpdatedNotifications(memberId: Long): ApiResult<List<UpdatedNotification>> {
        return withContext(dispatcher) {
            handleApi(
                execute = { notificationService.getUpdatedNotifications(memberId) },
                mapToDomain = List<UpdatedNotificationApiModel>::toData,
            )
        }
    }

    override suspend fun updateUpdatedNotificationReadStatus(notificationId: Long): ApiResult<Unit> {
        return withContext(dispatcher) {
            handleApi(
                execute = { notificationService.updateUpdatedNotificationReadStatus(notificationId) },
                mapToDomain = { },
            )
        }
    }

    override suspend fun deleteUpdatedNotifications(notificationIds: List<Long>): ApiResult<Unit> {
        return withContext(dispatcher) {
            handleApi(
                execute = {
                    notificationService.deleteNotification(
                        UpdatedNotificationDeleteRequestModel(notificationIds),
                    )
                },
                mapToDomain = { },
            )
        }
    }

    override suspend fun reportRecruitmentNotification(
        recruitmentNotificationId: Long,
        senderId: Long,
        reporterId: Long,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                notificationService.reportRecruitmentNotification(
                    ReportRequestBody.createRecruitmentNotificationReport(
                        recruitmentNotificationId = recruitmentNotificationId,
                        senderId = senderId,
                        reporterId = reporterId,
                    ),
                )
            },
            mapToDomain = {},
        )
    }
}
