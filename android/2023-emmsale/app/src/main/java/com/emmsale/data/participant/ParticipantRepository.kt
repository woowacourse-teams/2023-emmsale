package com.emmsale.data.participant

import com.emmsale.data.common.ApiResult

interface ParticipantRepository {

    suspend fun fetchEventParticipants(
        eventId: Long,
    ): ApiResult<List<Participant>>

    suspend fun saveParticipant(eventId: Long): ApiResult<Unit>
    suspend fun deleteParticipant(eventId: Long): ApiResult<Unit>

    suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResult<Unit>

    suspend fun checkParticipationStatus(eventId: Long): ApiResult<Boolean>
}
