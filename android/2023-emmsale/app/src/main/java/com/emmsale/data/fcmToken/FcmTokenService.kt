package com.emmsale.data.fcmToken

import com.emmsale.data.fcmToken.dto.FcmTokenApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenService {
    @POST("/notification/token")
    suspend fun saveFcmToken(@Body fcmToken: FcmTokenApiModel): Response<Unit>
}
