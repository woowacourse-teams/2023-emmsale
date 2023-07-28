package com.emmsale.data.login

import com.emmsale.data.common.ApiResult

interface LoginRepository {
    suspend fun saveGithubCode(code: String): ApiResult<Login>
}
