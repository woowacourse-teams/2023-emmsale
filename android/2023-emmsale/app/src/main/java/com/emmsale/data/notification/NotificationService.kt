package com.emmsale.data.notification

import retrofit2.Response
import retrofit2.http.GET

interface NotificationService {
    @GET("/request-notifications")
    suspend fun getNotifications(): Response<List<NotificationApiModel>>
}
