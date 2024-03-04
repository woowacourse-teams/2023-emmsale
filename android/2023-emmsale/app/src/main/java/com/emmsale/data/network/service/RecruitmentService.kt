package com.emmsale.data.network.service

import com.emmsale.data.network.apiModel.request.RecruitmentCreateRequest
import com.emmsale.data.network.apiModel.request.RecruitmentDeleteRequest
import com.emmsale.data.network.apiModel.request.RecruitmentReportCreateRequest
import com.emmsale.data.network.apiModel.response.RecruitmentReportResponse
import com.emmsale.data.network.apiModel.response.RecruitmentResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitmentService {

    @GET("/events/{eventId}/recruitment-posts")
    suspend fun getRecruitments(
        @Path("eventId") eventId: Long,
    ): ApiResponse<List<RecruitmentResponse>>

    @GET("/events/{eventId}/recruitment-posts/{recruitment-post-id}")
    suspend fun getRecruitment(
        @Path("eventId") eventId: Long,
        @Path("recruitment-post-id") recruitmentId: Long,
    ): ApiResponse<RecruitmentResponse>

    @GET("/events/recruitment-posts")
    suspend fun getMemberRecruitments(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<RecruitmentResponse>>

    @POST("/events/{eventId}/recruitment-posts")
    suspend fun postRecruitment(
        @Path("eventId") eventId: Long,
        @Body recruitmentCreateRequest: RecruitmentCreateRequest,
    ): ApiResponse<Unit>

    @PUT("/events/{eventId}/recruitment-posts/{recruitment-post-id}")
    suspend fun editRecruitment(
        @Path("eventId") eventId: Long,
        @Path("recruitment-post-id") recruitmentId: Long,
        @Body recruitmentDeleteRequest: RecruitmentDeleteRequest,
    ): ApiResponse<Unit>

    @DELETE("/events/{eventId}/recruitment-posts/{post-id}")
    suspend fun deleteRecruitment(
        @Path("eventId") eventId: Long,
        @Path("post-id") recruitmentId: Long,
    ): ApiResponse<Unit>

    @GET("/events/{eventId}/recruitment-posts/already-recruitment")
    suspend fun checkIsAlreadyPostRecruitment(
        @Path("eventId") eventId: Long,
        @Query("member-id") memberId: Long,
    ): ApiResponse<Boolean>

    @POST("/reports")
    suspend fun reportRecruitment(
        @Body recruitmentReportCreateRequest: RecruitmentReportCreateRequest,
    ): ApiResponse<RecruitmentReportResponse>
}
