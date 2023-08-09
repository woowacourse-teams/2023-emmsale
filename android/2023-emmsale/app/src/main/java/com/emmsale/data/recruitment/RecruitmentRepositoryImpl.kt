package com.emmsale.data.recruitment

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.recruitment.dto.CompanionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import com.emmsale.data.recruitment.dto.RecruitmentRequestBody
import com.emmsale.data.recruitment.dto.toData
import com.emmsale.data.uid.UidRepository

class RecruitmentRepositoryImpl(
    private val recruitmentService: RecruitmentService,
    uidRepository: UidRepository,
) : RecruitmentRepository {

    private val currentUid = uidRepository.getCurrentUid()

    override suspend fun fetchEventRecruitments(eventId: Long): ApiResult<List<Recruitment>> {
        return handleApi(
            execute = { recruitmentService.getRecruitment(eventId) },
            mapToDomain = List<RecruitmentApiModel>::toData,
        )
    }

    override suspend fun saveRecruitment(eventId: Long): ApiResult<Unit> {
        val requestBody = RecruitmentRequestBody(currentUid)
        return handleApi(
            execute = { recruitmentService.saveRecruitment(eventId, requestBody) },
            mapToDomain = { },
        )
    }

    override suspend fun deleteRecruitment(eventId: Long): ApiResult<Unit> {
        return handleApi(
            execute = {
                recruitmentService.deleteRecruitment(
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
            execute = { recruitmentService.postCompanion(requestBody) },
            mapToDomain = {},
        )
    }

    override suspend fun checkParticipationStatus(eventId: Long): ApiResult<Boolean> {
        return handleApi(
            execute = { recruitmentService.checkIsRecruitmented(eventId, currentUid) },
            mapToDomain = { true },
        )
    }
}
