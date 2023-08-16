package com.emmsale.data.notification

import com.emmsale.data.notification.recruitment.dto.RecruitmentNotificationApiModel
import com.emmsale.data.notification.recruitment.dto.RecruitmentStatusUpdateRequestModel
import com.emmsale.data.notification.updated.dto.UpdatedNotificationApiModel
import com.emmsale.data.notification.updated.dto.UpdatedNotificationDeleteRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    @GET("/request-notifications")
    suspend fun getRecruitmentNotifications(
        @Query("member-id") memberId: Long,
    ): Response<List<RecruitmentNotificationApiModel>>

    @PATCH("/request-notifications/{request-notification-id}/status")
    suspend fun updateRecruitmentStatus(
        @Path("request-notification-id") notificationId: Long,
        @Body newStatus: RecruitmentStatusUpdateRequestModel,
    ): Response<Unit>

    @PATCH("/request-notifications/{request-notification-id}/read")
    suspend fun updateNotificationReadStatus(
        @Path("request-notification-id") notificationId: Long,
    ): Response<Unit>

    @GET("/update-notifications")
    suspend fun getUpdatedNotifications(
        @Query("member-id") memberId: Long,
    ): Response<List<UpdatedNotificationApiModel>>

    @PUT("/update-notifications/{update-notification-id}/read")
    suspend fun updateUpdatedNotificationReadStatus(
        @Path("update-notification-id") notificationId: Long,
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "/update-notifications", hasBody = true)
    suspend fun deleteNotification(
        @Body notificationIds: UpdatedNotificationDeleteRequestModel,
    ): Response<Unit>
}
