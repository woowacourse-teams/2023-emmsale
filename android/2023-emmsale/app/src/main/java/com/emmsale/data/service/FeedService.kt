package com.emmsale.data.service

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedService {

    @GET("/feeds/{feedId}")
    fun getFeed(
        @Path("feedId") feedId: Long,
    ): ApiResponse<FeedDetailResponse>
}
