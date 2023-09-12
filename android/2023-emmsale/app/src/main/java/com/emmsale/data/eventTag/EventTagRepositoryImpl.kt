package com.emmsale.data.eventTag

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.handleApi
import com.emmsale.data.eventTag.mapper.toData
import com.emmsale.data.eventTag.remote.EventTagRemoteDataSource
import com.emmsale.data.eventTag.remote.dto.EventTagApiModel
import com.emmsale.data.eventTag.remote.dto.InterestEventTagUpdateRequestApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventTagRepositoryImpl(
    private val eventTagRemoteDataSource: EventTagRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : EventTagRepository {
    private val cachedEventTags = mutableListOf<EventTag>()

    override suspend fun getEventTags(): ApiResponse<List<EventTag>> = withContext(dispatcher) {
        if (cachedEventTags.isNotEmpty()) {
            return@withContext Success(cachedEventTags.map(EventTag::copy))
        }

        eventTagRemoteDataSource
            .getEventTags()
            .map(List<EventTagApiModel>::toData)
            .apply { if (this is Success) cacheEventTags(data) }
    }

    override suspend fun getEventTagByIds(ids: Array<Long>): ApiResponse<List<EventTag>> =
        withContext(dispatcher) {
            eventTagRemoteDataSource
                .getEventTags()
                .map { eventTags ->
                    eventTags.filter { eventTag -> ids.contains(eventTag.id) }
                }
                .map(List<EventTagApiModel>::toData)
        }

    override suspend fun getInterestEventTags(memberId: Long): ApiResult<List<EventTag>> {
        if (cachedEventTags.isNotEmpty()) return ApiSuccess(cachedEventTags.map(EventTag::copy))

        val result = withContext(dispatcher) {
            handleApi(
                execute = { eventTagRemoteDataSource.getInterestEventTags(memberId) },
                mapToDomain = List<EventTagApiModel>::toData,
            )
        }
        if (result is ApiSuccess) cacheEventTags(result.data)

        return result
    }

    override suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResult<Unit> {
        val result = withContext(dispatcher) {
            handleApi(
                execute = {
                    eventTagRemoteDataSource.updateInterestEventTags(
                        InterestEventTagUpdateRequestApiModel(interestEventTags.map { it.id }),
                    )
                },
                mapToDomain = { },
            )
        }
        if (result is ApiSuccess) cacheEventTags(interestEventTags)

        return result
    }

    private fun cacheEventTags(interestEventTags: List<EventTag>) {
        cachedEventTags.clear()
        cachedEventTags.addAll(interestEventTags)
    }
}
