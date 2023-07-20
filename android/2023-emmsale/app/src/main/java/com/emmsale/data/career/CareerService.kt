package com.emmsale.data.career

import com.emmsale.data.career.dto.CareerApiModel
import retrofit2.Response
import retrofit2.http.GET

interface CareerService {
    @GET("/careers")
    suspend fun getCareers(): Response<List<CareerApiModel>>
}
