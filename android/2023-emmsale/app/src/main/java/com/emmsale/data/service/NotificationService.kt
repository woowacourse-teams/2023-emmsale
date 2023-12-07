package com.emmsale.data.service

import com.emmsale.data.apiModel.request.NotificationListDeleteRequest
import com.emmsale.data.apiModel.response.NotificationResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<NotificationResponse>>

    @PATCH("/notifications/{notification-id}/read")
    suspend fun readNotification(
        @Path("notification-id") notificationId: Long,
    ): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "/notifications", hasBody = true)
    suspend fun deleteNotification(
        @Body notificationIds: NotificationListDeleteRequest,
    ): ApiResponse<Unit>
}
