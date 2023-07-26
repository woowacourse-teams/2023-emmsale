package com.emmsale.data.fcmToken

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.fcmToken.dto.FcmTokenApiModel

class FcmTokenRepositoryImpl(
    private val fcmTokenService: FcmTokenService,
) : FcmTokenRepository {
    override suspend fun saveFcmToken(fcmToken: FcmToken): ApiResult<Unit> {
        return when (val tokenSavedResult =
            handleApi { fcmTokenService.saveFcmToken(FcmTokenApiModel.from(fcmToken)) }) {
            is ApiSuccess -> ApiSuccess(tokenSavedResult.data)
            is ApiException -> ApiException(tokenSavedResult.e)
            is ApiError -> ApiError(tokenSavedResult.code, tokenSavedResult.message)
        }
    }
}
