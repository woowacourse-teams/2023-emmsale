package com.emmsale.data.service

import com.emmsale.data.apiModel.response.ActivitiesApiModel
import com.emmsale.data.apiModel.response.MemberActivitiesApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ActivityService {
    @GET("/activities")
    suspend fun getActivities(): Response<List<ActivitiesApiModel>>

    @GET("/members/{memberId}/activities")
    suspend fun getActivities(@Path("memberId") memberId: Long): Response<List<MemberActivitiesApiModel>>
}
