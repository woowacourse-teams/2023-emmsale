package com.emmsale.data.service

import com.emmsale.data.apiModel.response.FcmTokenResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenService {
    @POST("/notifications/token")
    suspend fun saveFcmToken(
        @Body fcmToken: FcmTokenResponse,
    ): ApiResponse<Unit>
}
