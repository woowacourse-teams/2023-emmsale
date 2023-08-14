package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.notification.recruitment.dto.RecruitmentNotificationApiModel
import com.emmsale.data.notification.recruitment.mapper.toData
import com.emmsale.data.notification.recruitment.mapper.toRequestModel
import com.emmsale.data.notification.updated.UpdatedNotification
import com.emmsale.data.notification.updated.dto.UpdatedNotificationApiModel
import com.emmsale.data.notification.updated.mapper.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
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

    companion object {
        private const val IN_PROGRESS = "IN_PROGRESS"
        private const val ACCEPT = "ACCEPT"
        private const val REJECT = "REJECT"
    }
}
