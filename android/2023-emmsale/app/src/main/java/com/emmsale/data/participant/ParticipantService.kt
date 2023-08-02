package com.emmsale.data.participant

import com.emmsale.data.participant.dto.CompanionRequestBody
import com.emmsale.data.participant.dto.ParticipantApiModel
import com.emmsale.data.participant.dto.ParticipantRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ParticipantService {

    @GET("events/{eventId}/participants")
    suspend fun getParticipants(
        @Path("eventId") eventId: Long,
    ): Response<List<ParticipantApiModel>>

    @POST("events/{eventId}/participants")
    suspend fun saveParticipant(
        @Path("eventId") eventId: Long,
        @Body participantRequestBody: ParticipantRequestBody,
    ): Response<Unit>

    @DELETE("events/{eventId}/participants")
    suspend fun deleteParticipant(
        @Path("eventId") eventId: Long,
        @Body participantRequestBody: ParticipantRequestBody,
    ): Response<Unit>

    @POST("notifications")
    suspend fun postCompanion(
        @Body companionRequestBody: CompanionRequestBody,
    ): Response<Unit>
}
