package com.emmsale.data.service

import com.emmsale.data.apiModel.request.RecruitmentCreateRequest
import com.emmsale.data.apiModel.request.RecruitmentDeleteRequest
import com.emmsale.data.apiModel.request.RecruitmentReportRequest
import com.emmsale.data.apiModel.request.RecruitmentRequestCreateRequest
import com.emmsale.data.apiModel.response.RecruitmentApiModel
import com.emmsale.data.apiModel.response.ReportApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitmentService {

    @GET("events/{eventId}/recruitment-posts")
    suspend fun getRecruitments(
        @Path("eventId") eventId: Long,
    ): Response<List<RecruitmentApiModel>>

    @GET("events/{eventId}/recruitment-posts/{recruitment-post-id}")
    suspend fun getRecruitment(
        @Path("eventId") eventId: Long,
        @Path("recruitment-post-id") recruitmentId: Long,
    ): Response<RecruitmentApiModel>

    @POST("events/{eventId}/recruitment-posts")
    suspend fun postRecruitment(
        @Path("eventId") eventId: Long,
        @Body recruitmentCreateRequest: RecruitmentCreateRequest,
    ): Response<Unit>

    @PUT("events/{eventId}/recruitment-posts/{recruitment-post-id}")
    suspend fun editRecruitment(
        @Path("eventId") eventId: Long,
        @Path("recruitment-post-id") recruitmentId: Long,
        @Body recruitmentDeleteRequest: RecruitmentDeleteRequest,
    ): Response<Unit>

    @DELETE("events/{eventId}/recruitment-posts/{post-id}")
    suspend fun deleteRecruitment(
        @Path("eventId") eventId: Long,
        @Path("post-id") recruitmentId: Long,
    ): Response<Unit>

    @GET("events/{eventId}/recruitment-posts/already-recruitment")
    suspend fun checkIsAlreadyPostRecruitment(
        @Path("eventId") eventId: Long,
        @Query("member-id") memberId: Long,
    ): Response<Boolean>

    @POST("request-notifications")
    suspend fun postCompanion(
        @Body recruitmentRequestCreateRequest: RecruitmentRequestCreateRequest,
    ): Response<Unit>

    @GET("request-notifications/existed")
    suspend fun checkIsAlreadyRequestCompanion(
        @Query("receiverId") receiverId: Long,
        @Query("eventId") eventId: Long,
        @Query("senderId") senderId: Long,
    ): Response<Boolean>

    @POST("reports")
    suspend fun reportRecruitment(
        @Body recruitmentReportRequest: RecruitmentReportRequest,
    ): Response<ReportApiModel>
}
