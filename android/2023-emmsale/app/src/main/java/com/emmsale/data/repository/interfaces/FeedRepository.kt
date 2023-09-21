package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.FeedDetail

interface FeedRepository {

    suspend fun getFeed(feedId: Long): ApiResponse<FeedDetail>

    suspend fun deleteFeed(feedId: Long): ApiResponse<Unit>
}
