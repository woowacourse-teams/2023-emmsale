package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse

interface FcmTokenRepository {

    suspend fun saveFcmToken(uid: Long, fcmToken: String): ApiResponse<Unit>
}
