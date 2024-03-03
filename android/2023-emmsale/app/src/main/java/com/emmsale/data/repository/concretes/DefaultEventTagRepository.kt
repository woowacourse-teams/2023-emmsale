package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.network.apiModel.response.EventTagResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.model.EventTag
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.network.service.EventTagService
import com.emmsale.data.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultEventTagRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val eventTagService: EventTagService,
) : EventTagRepository {

    override suspend fun getEventTags(): ApiResponse<List<EventTag>> = withContext(dispatcher) {
        eventTagService
            .getEventTags()
            .map(List<EventTagResponse>::toData)
    }

    override suspend fun getEventTagByIds(tagIds: Array<Long>): ApiResponse<List<EventTag>> =
        withContext(dispatcher) {
            eventTagService
                .getEventTags()
                .map { eventTags ->
                    eventTags.filter { eventTag -> tagIds.contains(eventTag.id) }
                }
                .map(List<EventTagResponse>::toData)
        }

    override suspend fun getInterestEventTags(memberId: Long): ApiResponse<List<EventTag>> =
        withContext(dispatcher) {
            eventTagService
                .getInterestEventTags(memberId)
                .map(List<EventTagResponse>::toData)
        }

    override suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResponse<Unit> =
        withContext(dispatcher) {
            eventTagService.updateInterestEventTags(
                InterestEventTagUpdateRequest(interestEventTags.map { it.id }),
            ).map { }
        }
}
