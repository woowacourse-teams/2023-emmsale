package com.emmsale.data.service

import com.emmsale.data.apiModel.request.MemberActivitiesAddRequest
import com.emmsale.data.apiModel.request.MemberBlockCreateRequest
import com.emmsale.data.apiModel.request.MemberCreateRequest
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequest
import com.emmsale.data.apiModel.response.ActivityResponse
import com.emmsale.data.apiModel.response.MemberResponse
import com.emmsale.data.apiModel.response.ProfileImageUrlResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart // <- 이 부분이 중요
    @PATCH("/members/{memberId}/profile")
    suspend fun updateMemberProfileImage(
        @Path("memberId") memberId: Long,
        @Part formDatas: MultipartBody.Part,
    ): ApiResponse<ProfileImageUrlResponse>

    @POST("/members/activities")
    suspend fun addMemberActivities(
        @Body body: MemberActivitiesAddRequest,
    ): ApiResponse<List<ActivityResponse>>

    @DELETE("/members/activities")
    suspend fun deleteMemberActivities(
        @Query("activity-ids") ids: List<Long>,
    ): ApiResponse<List<ActivityResponse>>

    @DELETE("/members/{memberId}")
    suspend fun deleteMember(
        @Path("memberId") memberId: Long,
    ): ApiResponse<Unit>

    @POST("/blocks")
    suspend fun blockMember(
        @Body memberBlockCreateRequest: MemberBlockCreateRequest,
    ): ApiResponse<Unit>
}
