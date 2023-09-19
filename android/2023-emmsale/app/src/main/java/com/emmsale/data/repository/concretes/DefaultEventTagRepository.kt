package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.dataSource.remote.EventTagRemoteDataSource
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.EventTagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultEventTagRepository(
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
            .map(List<EventTagResponse>::toData)
            .apply { if (this is Success) cacheEventTags(data) }
    }

    override suspend fun getEventTagByIds(tagIds: Array<Long>): ApiResponse<List<EventTag>> =
        withContext(dispatcher) {
            eventTagRemoteDataSource
                .getEventTags()
                .map { eventTags ->
                    eventTags.filter { eventTag -> tagIds.contains(eventTag.id) }
                }
                .map(List<EventTagResponse>::toData)
        }

    override suspend fun getInterestEventTags(memberId: Long): ApiResponse<List<EventTag>> {
        if (cachedEventTags.isNotEmpty()) {
            return Success(cachedEventTags.map(EventTag::copy))
        }
        val apiResponse = withContext(dispatcher) {
            eventTagRemoteDataSource
                .getInterestEventTags(memberId)
                .map(List<EventTagResponse>::toData)
        }

        if (apiResponse is Success) {
            cacheEventTags(apiResponse.data)
        }
        return apiResponse
    }

    override suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResponse<Unit> {
        val apiResponse = withContext(dispatcher) {
            eventTagRemoteDataSource.updateInterestEventTags(
                InterestEventTagUpdateRequest(interestEventTags.map { it.id }),
            ).map { }
        }

        if (apiResponse is Success) {
            cacheEventTags(interestEventTags)
        }
        return apiResponse
    }

    private fun cacheEventTags(interestEventTags: List<EventTag>) {
        cachedEventTags.clear()
        cachedEventTags.addAll(interestEventTags)
    }
}
