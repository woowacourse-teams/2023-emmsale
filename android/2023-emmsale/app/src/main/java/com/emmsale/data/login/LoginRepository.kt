package com.emmsale.data.login

import com.emmsale.data.common.ApiResult
import com.emmsale.data.token.Token

interface LoginRepository {
    suspend fun login(): ApiResult<Login>
}
