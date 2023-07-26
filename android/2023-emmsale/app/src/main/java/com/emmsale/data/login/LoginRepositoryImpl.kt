package com.emmsale.data.login

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class LoginRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val loginService: LoginService,
) : LoginRepository {
    override suspend fun saveGithubCode(code: String): ApiResult<Login> =
        withContext(coroutineScope.coroutineContext + dispatcher) {
            when (val loginResponse = handleApi { loginService.saveGithubCode(code) }) {
                is ApiSuccess -> ApiSuccess(Login.from(loginResponse.data))
                is ApiError -> ApiError(loginResponse.code, loginResponse.message)
                is ApiException -> ApiException(loginResponse.e)
            }
        }
}
