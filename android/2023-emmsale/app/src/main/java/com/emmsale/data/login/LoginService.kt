package com.emmsale.data.login

import com.emmsale.data.login.dto.LoginApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {
    @GET("/login/github/callback")
    suspend fun saveGithubCode(
        @Query("code") code: String
    ): Response<LoginApiModel>
}
