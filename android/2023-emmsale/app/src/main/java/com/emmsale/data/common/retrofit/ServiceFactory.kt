package com.emmsale.data.common.retrofit

import android.content.Context
import com.emmsale.BuildConfig
import com.emmsale.data.common.retrofit.callAdapter.KerdyCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ServiceFactory(context: Context) {
    private val json = Json {
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }
    private val jsonMediaType = "application/json".toMediaType()
    private val jsonConverterFactory = json.asConverterFactory(jsonMediaType)

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(AuthInterceptor(context))
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonConverterFactory)
        .addCallAdapterFactory(KerdyCallAdapterFactory())
        .client(okhttpClient)
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
    }
}
