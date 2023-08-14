package com.emmsale.data.member

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

interface MemberService {

    @GET("members/{memberId}")
    suspend fun getMember(@Path("memberId") memberId: Long): Response<MemberApiModel>

    @POST("/members")
    suspend fun updateMember(@Body member: MemberUpdateRequestBody): Response<Unit>

    @PUT("/members/description")
    suspend fun updateMemberDescription(@Body memberDescriptionUpdateRequestBody: MemberDescriptionUpdateRequestBody): Response<Unit>

    @DELETE("/members/{memberId}")
    suspend fun deleteMember(@Path("memberId") memberId: Long): Response<Unit>
}
