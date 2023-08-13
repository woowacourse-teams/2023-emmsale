package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.notification.mapper.toData
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
                execute = { notificationService.getNotifications() },
                mapToDomain = List<RecruitmentNotificationApiModel>::toData,
            )
        }

    override suspend fun updateRecruitmentAcceptedStatus(
        notificationId: Long,
        recruitmentStatus: RecruitmentNotificationStatus,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                val recruitingState = convertRequestApiString(recruitmentStatus)
                notificationService.updateRecruitmentAcceptedStatus(notificationId, recruitingState)
            },
            mapToDomain = { },
        )
    }

    private fun convertRequestApiString(recruitmentStatus: RecruitmentNotificationStatus) =
        when (recruitmentStatus) {
            RecruitmentNotificationStatus.ACCEPTED -> ACCEPT
            RecruitmentNotificationStatus.REJECTED -> REJECT
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

    companion object {
        private const val ACCEPT = "ACCEPT"
        private const val REJECT = "REJECT"
    }
}
