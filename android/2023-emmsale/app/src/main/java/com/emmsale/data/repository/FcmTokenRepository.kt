package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult

interface FcmTokenRepository {
    suspend fun saveFcmToken(uid: Long, fcmToken: String): ApiResult<Unit>
}
