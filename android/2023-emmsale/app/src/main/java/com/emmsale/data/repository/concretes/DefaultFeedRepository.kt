package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.apiModel.response.FeedResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Feed
import com.emmsale.data.model.FeedDetail
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.service.FeedService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class DefaultFeedRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val feedService: FeedService,
) : FeedRepository {

    override suspend fun getFeeds(eventId: Long): ApiResponse<List<Feed>> {
        return feedService
            .getFeeds(eventId)
            .map { it.toData() }
    }

    override suspend fun getFeed(
        feedId: Long,
    ): ApiResponse<FeedDetail> = withContext(dispatcher) {
        feedService
            .getFeed(feedId)
            .map(FeedDetailResponse::toData)
    }

    override suspend fun getFeed2(
        feedId: Long,
    ): ApiResponse<Feed> = withContext(dispatcher) {
        feedService
            .getFeed2(feedId)
            .map(FeedResponse::toData)
    }

    override suspend fun deleteFeed(
        feedId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        feedService.deleteFeed(feedId)
    }

    override suspend fun uploadFeed(
        eventId: Long,
        title: String,
        content: String,
        imageFiles: List<File>,
    ): ApiResponse<Long> {
        val imageRequestBodies = imageFiles.map { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                IMAGES_KEY,
                file.name,
                requestFile,
            )
        }

        val contentRequestBodies = HashMap<String, RequestBody>()
        contentRequestBodies[EVENT_ID_KEY] =
            eventId.toString().toRequestBody("application/json".toMediaTypeOrNull())
        contentRequestBodies[TITLE_KEY] =
            title.toRequestBody("application/json".toMediaTypeOrNull())
        contentRequestBodies[CONTENT_KEY] =
            content.toRequestBody("application/json".toMediaTypeOrNull())
        return feedService.uploadFeed(contentRequestBodies, imageRequestBodies)
    }

    companion object {
        const val EVENT_ID_KEY = "eventId"
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "content"
        const val IMAGES_KEY = "images"
    }
}
