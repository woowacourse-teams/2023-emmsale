package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.apiModel.response.NotificationResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.notification.Notification
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

    override suspend fun getNotifications(
        memberId: Long,
    ): ApiResponse<List<Notification>> = withContext(dispatcher) {
        notificationService
            .getNotifications(memberId)
            .map(List<NotificationResponse>::toData)
    }

    override suspend fun readNotification(
        notificationId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.readNotification(notificationId)
    }

    override suspend fun deleteNotifications(
        notificationIds: List<Long>,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        notificationService.deleteNotification(
            NotificationListDeleteRequest(notificationIds),
        )
    }
}
