package com.emmsale.data.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationService {
    @GET("/request-notifications")
    suspend fun getNotifications(): Response<List<NotificationApiModel>>

    @PATCH("/request-notifications/{request-notification-id}/status")
    suspend fun updateRecruitmentAcceptedStatus(
        @Path("request-notification-id") notificationId: Long,
        newStatus: String,
    ): Response<Unit>

    @PATCH("/request-notifications/{request-notification-id}/read")
    suspend fun updateNotificationReadStatus(
        @Path("request-notification-id") notificationId: Long,
        isRead: Boolean,
    ): Response<Unit>
}
