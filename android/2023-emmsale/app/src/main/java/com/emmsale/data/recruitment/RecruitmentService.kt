package com.emmsale.data.recruitment

import com.emmsale.data.recruitment.dto.CompanionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import com.emmsale.data.recruitment.dto.RecruitmentRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitmentService {

    @GET("events/{eventId}/participants")
    suspend fun getRecruitment(
        @Path("eventId") eventId: Long,
    ): Response<List<RecruitmentApiModel>>

    @POST("events/{eventId}/participants")
    suspend fun saveRecruitment(
        @Path("eventId") eventId: Long,
        @Body recruitmentRequestBody: RecruitmentRequestBody,
    ): Response<Unit>

    @DELETE("events/{eventId}/participants")
    suspend fun deleteRecruitment(
        @Path("eventId") eventId: Long,
        @Query("member-id") memberId: Long,
    ): Response<Unit>

    @GET("events/{eventId}/participants/already-participate")
    suspend fun checkIsRecruitmented(
        @Path("eventId") eventId: Long,
        @Query("member-id") memberId: Long,
    ): Response<Boolean>

    @POST("notifications")
    suspend fun postCompanion(
        @Body companionRequestBody: CompanionRequestBody,
    ): Response<Unit>
}
