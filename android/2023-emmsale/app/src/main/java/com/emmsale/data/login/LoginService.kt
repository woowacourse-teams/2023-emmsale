package com.emmsale.data.login

import com.emmsale.data.login.dto.LoginApiModel
import retrofit2.Response

interface LoginService {
    fun login(): Response<LoginApiModel>
}
