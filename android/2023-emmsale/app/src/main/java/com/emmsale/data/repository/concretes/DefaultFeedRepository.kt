package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.FeedDetail
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.service.FeedService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultFeedRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val feedService: FeedService,
) : FeedRepository {

    override suspend fun getFeed(
        feedId: Long,
    ): ApiResponse<FeedDetail> = withContext(dispatcher) {
        feedService
            .getFeed(feedId)
            .map(FeedDetailResponse::toData)
    }

    override suspend fun deleteFeed(
        feedId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        feedService.deleteFeed(feedId)
    }
}
