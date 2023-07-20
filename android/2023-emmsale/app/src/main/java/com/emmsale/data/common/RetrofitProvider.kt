package com.emmsale.data.common

import com.emmsale.data.career.CareerService
import com.emmsale.data.login.LoginService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    private const val BASE_URL = "http://13.125.212.56/"

    private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    private val json = Json {
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }
    private val jsonConverterFactory = json.asConverterFactory(JSON_MEDIA_TYPE)

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonConverterFactory)
        .client(okhttpClient)
        .build()

    val loginService: LoginService = retrofit.create(LoginService::class.java)
    val careerService: CareerService = retrofit.create(CareerService::class.java)
}
