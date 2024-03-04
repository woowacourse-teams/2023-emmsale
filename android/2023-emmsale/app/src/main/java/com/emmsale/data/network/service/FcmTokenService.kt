package com.emmsale.data.network.service

import com.emmsale.data.network.apiModel.response.FcmTokenResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenService {
    @POST("/notifications/token")
    suspend fun saveFcmToken(
        @Body fcmToken: FcmTokenResponse,
    ): ApiResponse<Unit>
}
