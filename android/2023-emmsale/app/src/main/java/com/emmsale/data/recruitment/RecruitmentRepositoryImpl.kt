package com.emmsale.data.recruitment

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.recruitment.dto.CompanionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import com.emmsale.data.recruitment.dto.RecruitmentDeletionRequestBody
import com.emmsale.data.recruitment.dto.RecruitmentPostingRequestBody
import com.emmsale.data.recruitment.mapper.toData
import com.emmsale.data.token.TokenRepository
import okhttp3.Headers

class RecruitmentRepositoryImpl(
    private val recruitmentService: RecruitmentService,
    tokenRepository: TokenRepository,
) : RecruitmentRepository {

    private val myUid =
        tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_LOGIN_ERROR_MESSAGE)

    override suspend fun getEventRecruitments(eventId: Long): ApiResult<List<Recruitment>> {
        return handleApi(
            execute = { recruitmentService.getRecruitments(eventId) },
            mapToDomain = List<RecruitmentApiModel>::toData,
        )
    }

    override suspend fun getEventRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResult<Recruitment> {
        return handleApi(
            execute = { recruitmentService.getRecruitment(eventId, recruitmentId) },
            mapToDomain = RecruitmentApiModel::toData,
        )
    }

    override suspend fun postRecruitment(eventId: Long, content: String): ApiResult<Long> {
        val requestBody = RecruitmentPostingRequestBody(memberId = myUid, content = content)
        val response = handleApi(
            execute = { recruitmentService.postRecruitment(eventId, requestBody) },
            mapToDomain = { },
        )
        return when (response) {
            is ApiSuccess -> {
                val recruitmentId = getRecruitmentIdFromHeaderLocation(response)
                ApiSuccess(recruitmentId, Headers.headersOf())
            }

            else -> response as ApiResult<Long>
        }
    }

    private fun getRecruitmentIdFromHeaderLocation(result: ApiSuccess<Unit>): Long {
        val locationHeader = result.header[HEADER_LOCATION] as CharSequence
        val regex = "/(\\d+)$".toRegex()
        val matchResult = regex.find(locationHeader)
        val extractedNumber = matchResult?.groups?.get(1)?.value?.toLongOrNull()
        return extractedNumber ?: throw IllegalStateException(LOCATION_HEADER_NOT_VALID)
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
        val requestBody = CompanionRequestBody(
            senderId = myUid,
            receiverId = memberId,
            eventId = eventId,
            message = message,
        )
        return handleApi(
            execute = { recruitmentService.postCompanion(requestBody) },
            mapToDomain = {},
        )
    }

    override suspend fun isAlreadyPostRecruitment(eventId: Long): ApiResult<Boolean> {
        return handleApi(
            execute = { recruitmentService.isAlreadyPostRecruitment(eventId, myUid) },
            mapToDomain = { it },
        )
    }
    companion object {
        private const val HEADER_LOCATION = "Location"
        private const val NOT_LOGIN_ERROR_MESSAGE = "로그인 되지 않아서 같이가요 정보를 가져올 수 없어요"
        private const val LOCATION_HEADER_NOT_VALID = "헤더 값이 제대로 오지 않았어요!! 서버와 명세를 맞춰보세요"
    }
}
