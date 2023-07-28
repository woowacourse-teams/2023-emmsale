package com.emmsale.data.fcmToken

import com.emmsale.data.common.ApiResult

interface FcmTokenRepository {
    suspend fun saveFcmToken(fcmToken: FcmToken): ApiResult<Unit>
}
