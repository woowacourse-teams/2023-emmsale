package com.emmsale.data.common

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ServiceFactory {
    private val jsonMediaType = "application/json".toMediaType()
    private val json = Json {
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }
    private val jsonConverterFactory = json.asConverterFactory(jsonMediaType)

    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonConverterFactory)
        .client(okhttpClient)
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    companion object {
        private const val BASE_URL = "https://kerdy.kro.kr/"
    }
}
