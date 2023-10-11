package com.emmsale.data.service

import com.emmsale.data.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.apiModel.request.RecruitmentNotificationReportCreateRequest
import com.emmsale.data.apiModel.request.RecruitmentNotificationStatusUpdateRequest
import com.emmsale.data.apiModel.response.NotificationReportResponse
import com.emmsale.data.apiModel.response.NotificationResponse
import com.emmsale.data.apiModel.response.RecruitmentNotificationResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    @GET("/request-notifications")
    suspend fun getRecruitmentNotifications(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<RecruitmentNotificationResponse>>

    @PATCH("/request-notifications/{request-notification-id}/status")
    suspend fun updateRecruitmentStatus(
        @Path("request-notification-id") notificationId: Long,
        @Body newStatus: RecruitmentNotificationStatusUpdateRequest,
    ): ApiResponse<Unit>

    @PATCH("/request-notifications/{request-notification-id}/read")
    suspend fun updateRecruitmentNotificationReadStatus(
        @Path("request-notification-id") notificationId: Long,
    ): ApiResponse<Unit>

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<NotificationResponse>>

    @PATCH("/notifications/{notification-id}/read")
    suspend fun updateNotificationReadStatus(
        @Path("notification-id") notificationId: Long,
    ): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "/notifications", hasBody = true)
    suspend fun deleteNotification(
        @Body notificationIds: NotificationListDeleteRequest,
    ): ApiResponse<Unit>

    @POST("/reports")
    suspend fun reportRecruitmentNotification(
        @Body recruitmentNotificationReportCreateRequest: RecruitmentNotificationReportCreateRequest,
    ): ApiResponse<NotificationReportResponse>
}
