package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.LoginResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Login
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.service.LoginService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
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
