package com.emmsale.data.service

import com.emmsale.data.apiModel.response.FcmTokenApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenService {
    @POST("/notifications/token")
    suspend fun saveFcmToken(
        @Body fcmToken: FcmTokenApiModel,
    ): Response<Unit>
}
