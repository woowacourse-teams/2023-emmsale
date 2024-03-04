package com.emmsale.data.network.service

import com.emmsale.data.network.apiModel.response.LoginResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @POST("/login/github/callback")
    suspend fun saveGithubCode(
        @Query("code") code: String,
    ): ApiResponse<LoginResponse>
}
