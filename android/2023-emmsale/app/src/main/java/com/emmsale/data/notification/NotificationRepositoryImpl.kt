package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.notification.recruitment.dto.RecruitmentNotificationApiModel
import com.emmsale.data.notification.recruitment.mapper.toData
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
    override suspend fun getRecruitmentNotifications(): ApiResult<List<RecruitmentNotification>> =
        withContext(dispatcher) {
            handleApi(
                execute = { notificationService.getRecruitmentNotifications() },
                mapToDomain = List<RecruitmentNotificationApiModel>::toData,
            )
        }

    override suspend fun updateRecruitmentStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentStatus,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                val recruitingState = convertRequestApiString(recruitmentStatus)
                notificationService.updateRecruitmentStatus(notificationId, recruitingState)
            },
            mapToDomain = { },
        )
    }

    private fun convertRequestApiString(recruitmentStatus: RecruitmentStatus) =
        when (recruitmentStatus) {
            RecruitmentStatus.ACCEPTED -> ACCEPT
            RecruitmentStatus.REJECTED -> REJECT
        }

    override suspend fun updateNotificationReadStatus(
        notificationId: Long,
        isRead: Boolean,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { notificationService.updateNotificationReadStatus(notificationId, isRead) },
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

    companion object {
        private const val ACCEPT = "ACCEPT"
        private const val REJECT = "REJECT"
    }
}
