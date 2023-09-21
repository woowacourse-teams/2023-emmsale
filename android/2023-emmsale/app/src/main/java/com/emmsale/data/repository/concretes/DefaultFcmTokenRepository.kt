package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.FcmTokenResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.service.FcmTokenService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultFcmTokenRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val fcmTokenService: FcmTokenService,
) : FcmTokenRepository {
    override suspend fun saveFcmToken(
        uid: Long,
        fcmToken: String,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        fcmTokenService.saveFcmToken(FcmTokenResponse(uid, fcmToken))
    }
}
