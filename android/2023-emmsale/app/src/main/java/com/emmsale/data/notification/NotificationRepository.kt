package com.emmsale.data.notification

import com.emmsale.data.common.ApiResult

interface NotificationRepository {
    suspend fun getNotifications(): ApiResult<List<Notification>>

    // TODO: 4차 스프린트에서 알림 삭제 기능 구현 예정
    // fun deleteNotification(notificationId: Long)
}
