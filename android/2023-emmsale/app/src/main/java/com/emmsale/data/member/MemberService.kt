package com.emmsale.data.member

import com.emmsale.data.member.dto.MemberActivitiesBindActivityTypeApiModel
import com.emmsale.data.member.dto.MemberApiModel
import com.emmsale.data.member.dto.MemberApiModel1
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberService {

    @GET("members/{memberId}")
    suspend fun fetchMember(@Path("memberId") memberId: Long): Response<MemberApiModel1>

    @GET("members/activities")
    suspend fun fetchActivities(): Response<List<MemberActivitiesBindActivityTypeApiModel>>

    @POST("/members")
    suspend fun updateMember(@Body member: MemberApiModel): Response<Unit>
}