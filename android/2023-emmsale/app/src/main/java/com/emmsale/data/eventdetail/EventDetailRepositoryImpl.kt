package com.emmsale.data.eventdetail

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import com.emmsale.data.eventdetail.mapper.toData
import com.emmsale.data.token.TokenRepository
import okhttp3.Headers

class EventDetailRepositoryImpl(
    private val eventDetailService: EventDetailService,
    tokenRepository: TokenRepository,
) : EventDetailRepository {

    private val myUid =
        tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_LOGIN_ERROR_MESSAGE)

    override suspend fun getEventDetail(eventId: Long): ApiResult<EventDetail> {
        return handleApi(
            execute = { eventDetailService.getEventDetail(eventId) },
            mapToDomain = EventDetailApiModel::toData,
        )
    }

    override suspend fun scrapEvent(eventId: Long): ApiResult<Unit> {
        return ApiSuccess(Unit, Headers.headersOf("Auth"))
    }

    companion object {
        private const val NOT_LOGIN_ERROR_MESSAGE = "로그인 되지 않아서 행사 정보를 가져올 수 없어요"
    }
}
