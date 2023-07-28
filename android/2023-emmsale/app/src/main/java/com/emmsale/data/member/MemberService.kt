package com.emmsale.data.member

import com.emmsale.data.member.dto.MemberApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberService {
    @POST("/members")
    suspend fun updateMember(@Body member: MemberApiModel): Response<Unit>
}
