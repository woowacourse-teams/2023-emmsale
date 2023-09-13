package com.emmsale.data.service

import com.emmsale.data.apiModel.response.LoginApiModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @POST("/login/github/callback")
    suspend fun saveGithubCode(
        @Query("code") code: String,
    ): Response<LoginApiModel>
}
