package com.emmsale.data.fcmToken

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.fcmToken.dto.FcmTokenApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FcmTokenRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val fcmTokenService: FcmTokenService,
) : FcmTokenRepository {
    override suspend fun saveFcmToken(fcmToken: FcmToken): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(fcmTokenService.saveFcmToken(FcmTokenApiModel.from(fcmToken))) { }
        }
}
