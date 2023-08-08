package com.emmsale.data.participant

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.participant.dto.CompanionRequestBody
import com.emmsale.data.participant.dto.ParticipantApiModel
import com.emmsale.data.participant.dto.ParticipantRequestBody
import com.emmsale.data.participant.dto.toData
import com.emmsale.data.uid.UidRepository

class ParticipantRepositoryImpl(
    private val participantService: ParticipantService,
    uidRepository: UidRepository,
) : ParticipantRepository {

    private val currentUid = uidRepository.getCurrentUid()

    override suspend fun fetchEventParticipants(eventId: Long): ApiResult<List<Participant>> {
        return handleApi(
            execute = { participantService.getParticipants(eventId) },
            mapToDomain = List<ParticipantApiModel>::toData,
        )
    }

    override suspend fun saveParticipant(eventId: Long): ApiResult<Unit> {
        val requestBody = ParticipantRequestBody(currentUid)
        return handleApi(
            execute = { participantService.saveParticipant(eventId, requestBody) },
            mapToDomain = { },
        )
    }

    override suspend fun deleteParticipant(eventId: Long): ApiResult<Unit> {
        return handleApi(
            execute = {
                participantService.deleteParticipant(
                    eventId,
                    currentUid,
                )
            },
            mapToDomain = {},
        )
    }

    override suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResult<Unit> {
        val requestBody = CompanionRequestBody(
            senderId = currentUid,
            receiverId = memberId,
            eventId = eventId,
            message = message,
        )
        return handleApi(
            execute = { participantService.postCompanion(requestBody) },
            mapToDomain = {},
        )
    }

    override suspend fun checkParticipationStatus(eventId: Long): ApiResult<Boolean> {
        return handleApi(
            execute = { participantService.checkIsParticipated(eventId, currentUid) },
            mapToDomain = { true },
        )
    }
}
