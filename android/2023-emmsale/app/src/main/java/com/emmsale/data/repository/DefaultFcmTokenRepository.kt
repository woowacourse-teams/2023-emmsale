package com.emmsale.data.repository

import com.emmsale.data.apiModel.response.FcmTokenApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.service.FcmTokenService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultFcmTokenRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val fcmTokenService: FcmTokenService,
) : FcmTokenRepository {
    override suspend fun saveFcmToken(uid: Long, fcmToken: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = { fcmTokenService.saveFcmToken(FcmTokenApiModel(uid, fcmToken)) },
                mapToDomain = { },
            )
        }
}
