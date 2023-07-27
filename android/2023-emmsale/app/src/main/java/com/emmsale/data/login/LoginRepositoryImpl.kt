package com.emmsale.data.login

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.login.dto.LoginApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val loginService: LoginService,
) : LoginRepository {
    override suspend fun saveGithubCode(code: String): ApiResult<Login> = withContext(dispatcher) {
        handleApi(loginService.saveGithubCode(code), LoginApiModel::toData)
    }
}
