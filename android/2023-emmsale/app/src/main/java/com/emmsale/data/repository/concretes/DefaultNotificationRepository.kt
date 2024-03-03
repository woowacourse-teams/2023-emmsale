package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.network.apiModel.response.NotificationResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.model.notification.Notification
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.network.service.NotificationService
import com.emmsale.data.network.di.IoDispatcher
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
