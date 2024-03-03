package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.response.FcmTokenResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.network.service.FcmTokenService
import com.emmsale.data.network.di.IoDispatcher
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
