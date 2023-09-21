package com.emmsale.data.repository.concretes

import android.util.Log
import com.emmsale.data.apiModel.request.RecruitmentCreateRequest
import com.emmsale.data.apiModel.request.RecruitmentDeleteRequest
import com.emmsale.data.apiModel.request.RecruitmentReportCreateRequest
import com.emmsale.data.apiModel.request.RecruitmentRequestCreateRequest
import com.emmsale.data.apiModel.response.RecruitmentResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Recruitment
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.data.service.RecruitmentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRecruitmentRepository @Inject constructor(
    private val recruitmentService: RecruitmentService,
    tokenRepository: TokenRepository,
) : RecruitmentRepository {
    private val myUid =
        tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_LOGIN_ERROR_MESSAGE)

    override suspend fun getEventRecruitments(
        eventId: Long,
    ): ApiResponse<List<Recruitment>> = recruitmentService
        .getRecruitments(eventId)
        .map(List<RecruitmentResponse>::toData)

    override suspend fun getEventRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResponse<Recruitment> = recruitmentService
        .getRecruitment(eventId, recruitmentId)
        .map(RecruitmentResponse::toData)

    override suspend fun postRecruitment(
        eventId: Long,
        content: String,
    ): ApiResponse<Long> = withContext(Dispatchers.IO) {
        val deferredResponse = async {
            recruitmentService.postRecruitment(
                eventId,
                RecruitmentCreateRequest(memberId = myUid, content = content),
            )
        }

        when (val response = deferredResponse.await()) {
            is Success -> {
                val recruitmentId = getRecruitmentIdFromResponse(response)
                Success((recruitmentId))
            }

            is Failure -> Failure(response.code, response.message)
            is NetworkError -> NetworkError
            is Unexpected -> Unexpected(response.error)
        }
    }

    private fun getRecruitmentIdFromResponse(result: Success<Unit>): Long {
        Log.d("wooseok", result.toString())
        val locationHeader = result.headers[HEADER_LOCATION] as CharSequence
        val regex = "/(\\d+)$".toRegex()
        val matchResult = regex.find(locationHeader)
        val extractedNumber = matchResult?.groups?.get(1)?.value?.toLongOrNull()
        return extractedNumber ?: throw IllegalStateException(LOCATION_HEADER_NOT_VALID)
    }

    override suspend fun deleteRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResponse<Unit> = recruitmentService.deleteRecruitment(
        eventId,
        recruitmentId,
    )

    override suspend fun editRecruitment(
        eventId: Long,
        recruitmentId: Long,
        content: String,
    ): ApiResponse<Unit> = recruitmentService.editRecruitment(
        eventId = eventId,
        recruitmentId = recruitmentId,
        recruitmentDeleteRequest = RecruitmentDeleteRequest(content),
    )

    override suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResponse<Unit> = recruitmentService.postCompanion(
        RecruitmentRequestCreateRequest(
            senderId = myUid,
            receiverId = memberId,
            eventId = eventId,
            message = message,
        ),
    )

    override suspend fun checkIsAlreadyRequestCompanion(
        eventId: Long,
        senderId: Long,
        receiverId: Long,
    ): ApiResponse<Boolean> = recruitmentService.checkIsAlreadyRequestCompanion(
        eventId = eventId,
        senderId = senderId,
        receiverId = receiverId,
    )

    override suspend fun checkIsAlreadyPostRecruitment(
        eventId: Long,
    ): ApiResponse<Boolean> = recruitmentService
        .checkIsAlreadyPostRecruitment(eventId, myUid)

    override suspend fun reportRecruitment(
        recruitmentId: Long,
        authorId: Long,
        reporterId: Long,
    ): ApiResponse<Unit> = recruitmentService
        .reportRecruitment(
            RecruitmentReportCreateRequest.create(
                recruitmentId = recruitmentId,
                authorId = authorId,
                reporterId = reporterId,
            ),
        )
        .map { }

    companion object {
        private const val HEADER_LOCATION = "Location"
        private const val NOT_LOGIN_ERROR_MESSAGE = "로그인 되지 않아서 같이가요 정보를 가져올 수 없어요"
        private const val LOCATION_HEADER_NOT_VALID = "헤더 값이 제대로 오지 않았어요!! 서버와 명세를 맞춰보세요"
    }
}
