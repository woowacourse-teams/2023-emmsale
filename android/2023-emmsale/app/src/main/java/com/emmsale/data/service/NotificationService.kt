package com.emmsale.data.service

import com.emmsale.data.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.apiModel.request.RecruitmentNotificationReportRequest
import com.emmsale.data.apiModel.request.RecruitmentNotificationStatusUpdateRequest
import com.emmsale.data.apiModel.response.RecruitmentNotificationApiModel
import com.emmsale.data.apiModel.response.ReportApiModel
import com.emmsale.data.apiModel.response.UpdatedNotificationApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
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
        @Body newStatus: RecruitmentNotificationStatusUpdateRequest,
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
        @Body notificationIds: NotificationListDeleteRequest,
    ): Response<Unit>

    @POST("/reports")
    suspend fun reportRecruitmentNotification(
        @Body recruitmentNotificationReportRequest: RecruitmentNotificationReportRequest,
    ): Response<ReportApiModel>
}