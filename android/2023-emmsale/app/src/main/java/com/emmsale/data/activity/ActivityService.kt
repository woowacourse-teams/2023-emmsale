package com.emmsale.data.activity

import com.emmsale.data.activity.dto.ActivitiesApiModel
import retrofit2.Response
import retrofit2.http.GET

interface ActivityService {
    @GET("/careers")
    suspend fun getActivities(): Response<List<ActivitiesApiModel>>
}
