package com.emmsale.data.recruitment

import com.emmsale.data.recruitment.dto.CompanionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import com.emmsale.data.recruitment.dto.RecruitmentDeletionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentPostingRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitmentService {

    @GET("events/{eventId}/recruitment-post")
    suspend fun getRecruitments(
        @Path("eventId") eventId: Long,
    ): Response<List<RecruitmentApiModel>>

    @POST("events/{eventId}/recruitment-post")
    suspend fun postRecruitment(
        @Path("eventId") eventId: Long,
        @Body recruitmentPostingRequestBody: RecruitmentPostingRequestBody,
    ): Response<Unit>

    @PUT("events/{eventId}/recruitment-post/{recruitment-post-id}")
    suspend fun editRecruitment(
        @Path("eventId") eventId: Long,
        @Path("recruitment-post-id") recruitmentId: Long,
        @Body recruitmentDeletionRequestBody: RecruitmentDeletionRequestBody,
    ): Response<Unit>

    @DELETE("events/{eventId}/recruitment-post")
    suspend fun deleteRecruitment(
        @Path("eventId") eventId: Long,
        @Query("post-id") recruitmentId: Long,
    ): Response<Unit>

    @GET("events/{eventId}/recruitment-post/already-recruitment")
    suspend fun checkHasWritingPermission(
        @Path("eventId") eventId: Long,
        @Query("member-id") memberId: Long,
    ): Response<Boolean>

    @POST("notifications")
    suspend fun postCompanion(
        @Body companionRequestBody: CompanionRequestBody,
    ): Response<Unit>
}
