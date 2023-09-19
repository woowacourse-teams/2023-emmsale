package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.FeedDetail
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.service.FeedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultFeedRepository(private val feedService: FeedService) : FeedRepository {
    override suspend fun getFeed(feedId: Long): ApiResponse<FeedDetail> =
        withContext(Dispatchers.IO) {
            feedService
                .getFeed(feedId)
                .map(FeedDetailResponse::toData)
        }
}
