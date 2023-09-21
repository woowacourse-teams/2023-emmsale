package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.dataSource.remote.EventTagRemoteDataSource
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultEventTagRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val eventTagRemoteDataSource: EventTagRemoteDataSource,
) : EventTagRepository {

    override suspend fun getEventTags(): ApiResponse<List<EventTag>> = withContext(dispatcher) {
        eventTagRemoteDataSource
            .getEventTags()
            .map(List<EventTagResponse>::toData)
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

    override suspend fun getInterestEventTags(memberId: Long): ApiResponse<List<EventTag>> =
        withContext(dispatcher) {
            eventTagRemoteDataSource
                .getInterestEventTags(memberId)
                .map(List<EventTagResponse>::toData)
        }

    override suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResponse<Unit> =
        withContext(dispatcher) {
            eventTagRemoteDataSource.updateInterestEventTags(
                InterestEventTagUpdateRequest(interestEventTags.map { it.id }),
            ).map { }
        }
}
