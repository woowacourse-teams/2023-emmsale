package com.emmsale.data.login

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.token.Token

class LoginRepositoryImpl(private val loginService: LoginService) : LoginRepository {
    override suspend fun login(): ApiResult<Login> {
        return when (val loginResponse = handleApi { loginService.login() }) {
            is ApiSuccess -> ApiSuccess(Login.from(loginResponse.data))
            is ApiError -> ApiError(loginResponse.code, loginResponse.message)
            is ApiException -> ApiException(loginResponse.e)
        }
    }
}
