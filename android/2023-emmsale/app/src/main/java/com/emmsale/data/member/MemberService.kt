package com.emmsale.data.member

import com.emmsale.data.activity.dto.MemberActivitiesApiModel
import com.emmsale.data.member.dto.BlockRequestBody
import com.emmsale.data.member.dto.MemberActivitiesUpdateRequestBody
import com.emmsale.data.member.dto.MemberApiModel
import com.emmsale.data.member.dto.MemberDescriptionUpdateRequestBody
import com.emmsale.data.member.dto.MemberUpdateRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MemberService {

    @GET("members/{memberId}")
    suspend fun getMember(@Path("memberId") memberId: Long): Response<MemberApiModel>

    @POST("/members")
    suspend fun updateMember(@Body member: MemberUpdateRequestBody): Response<Unit>

    @PUT("/members/description")
    suspend fun updateMemberDescription(@Body memberDescriptionUpdateRequestBody: MemberDescriptionUpdateRequestBody): Response<Unit>

    @POST("/members/activities")
    suspend fun addMemberActivities(@Body memberActivitiesUpdateRequestBody: MemberActivitiesUpdateRequestBody): Response<Unit>

    @DELETE("/members/activities?{ids}")
    suspend fun deleteMemberActivities(@Query("ids") ids: List<Long>): Response<List<MemberActivitiesApiModel>>

    @DELETE("/members/{memberId}")
    suspend fun deleteMember(@Path("memberId") memberId: Long): Response<Unit>

    @POST("/blocks")
    suspend fun blockMember(@Body blockRequestBody: BlockRequestBody): Response<Unit>
}
