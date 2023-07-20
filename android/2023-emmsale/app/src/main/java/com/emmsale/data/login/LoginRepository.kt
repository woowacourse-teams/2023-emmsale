package com.emmsale.data.login

import com.emmsale.data.common.ApiResult

interface LoginRepository {
    suspend fun login(): ApiResult<Login>
}
