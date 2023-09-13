package com.emmsale.data.repository

import com.emmsale.data.apiModel.response.LoginApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Login
import com.emmsale.data.service.LoginService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultLoginRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val loginService: LoginService,
) : LoginRepository {
    override suspend fun saveGithubCode(code: String): ApiResult<Login> = withContext(dispatcher) {
        handleApi(
            execute = { loginService.saveGithubCode(code) },
            mapToDomain = LoginApiModel::toData,
        )
    }
}