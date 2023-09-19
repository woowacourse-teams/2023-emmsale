package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.LoginResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Login
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.service.LoginService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultLoginRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val loginService: LoginService,
) : LoginRepository {
    override suspend fun saveGithubCode(
        code: String,
    ): ApiResponse<Login> = withContext(dispatcher) {
        loginService
            .saveGithubCode(code)
            .map(LoginResponse::toData)
    }
}
