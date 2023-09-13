package com.emmsale.data.service

import com.emmsale.data.apiModel.request.MemberActivitiesUpdateRequestBody
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequestBody
import com.emmsale.data.apiModel.request.MemberOpenProfileUrlUpdateRequestBody
import com.emmsale.data.apiModel.request.MemberUpdateRequestBody
import com.emmsale.data.apiModel.response.BlockRequestBody
import com.emmsale.data.apiModel.response.MemberActivitiesApiModel
import com.emmsale.data.apiModel.response.MemberApiModel
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

    @PUT("/members/open-profile-url")
    suspend fun updateMemberOpenProfileUrl(
        @Body memberOpenProfileUrlUpdateRequestBody: MemberOpenProfileUrlUpdateRequestBody,
    ): Response<Unit>

    @POST("/members/activities")
    suspend fun addMemberActivities(@Body memberActivitiesUpdateRequestBody: MemberActivitiesUpdateRequestBody): Response<Unit>

    @DELETE("/members/activities")
    suspend fun deleteMemberActivities(@Query("activity-ids") ids: List<Long>): Response<List<MemberActivitiesApiModel>>

    @DELETE("/members/{memberId}")
    suspend fun deleteMember(@Path("memberId") memberId: Long): Response<Unit>

    @POST("/blocks")
    suspend fun blockMember(@Body blockRequestBody: BlockRequestBody): Response<Unit>
}
