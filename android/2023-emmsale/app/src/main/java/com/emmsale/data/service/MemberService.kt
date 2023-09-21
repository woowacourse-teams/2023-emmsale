package com.emmsale.data.service

import com.emmsale.data.apiModel.request.MemberActivitiesUpdateRequest
import com.emmsale.data.apiModel.request.MemberBlockCreateRequest
import com.emmsale.data.apiModel.request.MemberCreateRequest
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequest
import com.emmsale.data.apiModel.request.MemberOpenProfileUrlUpdateRequest
import com.emmsale.data.apiModel.response.MemberActivitiesResponse
import com.emmsale.data.apiModel.response.MemberResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MemberService {

    @GET("/members/{memberId}")
    suspend fun getMember(
        @Path("memberId") memberId: Long,
    ): ApiResponse<MemberResponse>

    @POST("/members")
    suspend fun updateMember(
        @Body member: MemberCreateRequest,
    ): ApiResponse<Unit>

    @PUT("/members/description")
    suspend fun updateMemberDescription(
        @Body memberDescriptionUpdateRequest: MemberDescriptionUpdateRequest,
    ): ApiResponse<Unit>

    @PUT("/members/open-profile-url")
    suspend fun updateMemberOpenProfileUrl(
        @Body memberOpenProfileUrlUpdateRequest: MemberOpenProfileUrlUpdateRequest,
    ): ApiResponse<Unit>

    @POST("/members/activities")
    suspend fun addMemberActivities(
        @Body memberActivitiesUpdateRequest: MemberActivitiesUpdateRequest,
    ): ApiResponse<Unit>

    @DELETE("/members/activities")
    suspend fun deleteMemberActivities(
        @Query("activity-ids") ids: List<Long>,
    ): ApiResponse<List<MemberActivitiesResponse>>

    @DELETE("/members/{memberId}")
    suspend fun deleteMember(
        @Path("memberId") memberId: Long,
    ): ApiResponse<Unit>

    @POST("/blocks")
    suspend fun blockMember(
        @Body memberBlockCreateRequest: MemberBlockCreateRequest,
    ): ApiResponse<Unit>
}