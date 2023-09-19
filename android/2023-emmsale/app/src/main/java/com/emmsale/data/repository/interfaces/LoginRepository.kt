package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Login

interface LoginRepository {

    suspend fun saveGithubCode(code: String): ApiResponse<Login>
}
