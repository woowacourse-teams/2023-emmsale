package com.emmsale.data.recruitment

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.recruitment.dto.CompanionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import com.emmsale.data.recruitment.dto.RecruitmentDeletionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentPostingRequestBody
import com.emmsale.data.recruitment.mapper.toData
import com.emmsale.data.token.TokenRepository

class RecruitmentRepositoryImpl(
    private val recruitmentService: RecruitmentService,
    private val tokenRepository: TokenRepository,
) : RecruitmentRepository {

    override suspend fun fetchEventRecruitments(eventId: Long): ApiResult<List<Recruitment>> {
        return handleApi(
            execute = { recruitmentService.getRecruitments(eventId) },
            mapToDomain = List<RecruitmentApiModel>::toData,
        )
    }

    override suspend fun postRecruitment(eventId: Long, content: String): ApiResult<Unit> {
        val currentUid = getCurrentUid()
        val requestBody = RecruitmentPostingRequestBody(memberId = currentUid, content = content)
        return handleApi(
            execute = { recruitmentService.postRecruitment(eventId, requestBody) },
            mapToDomain = { },
        )
    }

    override suspend fun deleteRecruitment(eventId: Long, recruitmentId: Long): ApiResult<Unit> {
        return handleApi(
            execute = {
                recruitmentService.deleteRecruitment(
                    eventId,
                    recruitmentId,
                )
            },
            mapToDomain = {},
        )
    }

    override suspend fun editRecruitment(
        eventId: Long,
        recruitmentId: Long,
        content: String,
    ): ApiResult<Unit> {
        return handleApi(
            execute = {
                recruitmentService.editRecruitment(
                    eventId = eventId,
                    recruitmentId = recruitmentId,
                    recruitmentDeletionRequestBody = RecruitmentDeletionRequestBody(content),
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
        val currentUid = getCurrentUid()
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

    override suspend fun checkHasWritingPermission(eventId: Long): ApiResult<Boolean> {
        val currentUid = getCurrentUid()
        return handleApi(
            execute = { recruitmentService.checkHasWritingPermission(eventId, currentUid) },
            mapToDomain = { it },
        )
    }

    private suspend fun getCurrentUid(): Long =
        tokenRepository.getToken()?.uid ?: throw IllegalStateException(
            NOT_LOGIN_ERROR_MESSAGE,
        )

    companion object {
        private const val NOT_LOGIN_ERROR_MESSAGE = "로그인 되지 않아서 같이가요 정보를 가져올 수 없어요"
    }
}
