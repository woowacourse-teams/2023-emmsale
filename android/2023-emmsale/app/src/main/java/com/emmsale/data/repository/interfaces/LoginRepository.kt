package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.Login

interface LoginRepository {

    suspend fun saveGithubCode(code: String): ApiResponse<Login>
}
