package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.Feed
import com.emmsale.data.model.FeedDetail
import java.io.File

interface FeedRepository {

    suspend fun getFeeds(eventId: Long): ApiResponse<List<Feed>>

    suspend fun getFeed(feedId: Long): ApiResponse<FeedDetail>

    suspend fun getFeed2(feedId: Long): ApiResponse<Feed>

    suspend fun deleteFeed(feedId: Long): ApiResponse<Unit>

    suspend fun uploadFeed(
        eventId: Long,
        title: String,
        content: String,
        imageFiles: List<File>,
    ): ApiResponse<Long>
}
