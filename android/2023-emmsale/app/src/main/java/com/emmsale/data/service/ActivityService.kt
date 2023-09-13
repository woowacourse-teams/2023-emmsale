package com.emmsale.data.service

import com.emmsale.data.apiModel.response.ActivitiesResponse
import com.emmsale.data.apiModel.response.MemberActivitiesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ActivityService {
    @GET("/activities")
    suspend fun getActivities(): Response<List<ActivitiesResponse>>

    @GET("/members/{memberId}/activities")
    suspend fun getActivities(
        @Path("memberId") memberId: Long,
    ): Response<List<MemberActivitiesResponse>>
}
