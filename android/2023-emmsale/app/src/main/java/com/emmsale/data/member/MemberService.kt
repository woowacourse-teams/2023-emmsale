package com.emmsale.data.member

import com.emmsale.data.member.dto.ActivitiesAssociatedByActivityTypeApiModel
import com.emmsale.data.member.dto.MemberApiModel
import com.emmsale.data.member.dto.MemberWithoutActivitiesApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberService {

    @GET("members/{memberId}")
    suspend fun getMember(@Path("memberId") memberId: Long): Response<MemberWithoutActivitiesApiModel>

    @GET("members/{memberId}/activities")
    suspend fun getActivities(@Path("memberId") memberId: Long): Response<List<ActivitiesAssociatedByActivityTypeApiModel>>

    @POST("/members")
    suspend fun updateMember(@Body member: MemberApiModel): Response<Unit>
}
