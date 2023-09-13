package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.Login

interface LoginRepository {
    suspend fun saveGithubCode(code: String): ApiResult<Login>
}
