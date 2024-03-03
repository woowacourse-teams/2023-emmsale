package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.response.LoginResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.model.Login
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.network.service.LoginService
import com.emmsale.data.network.di.IoDispatcher
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
