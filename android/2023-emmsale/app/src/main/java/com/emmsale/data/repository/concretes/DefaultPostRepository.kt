package com.emmsale.data.repository.concretes

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Post
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.data.service.PostService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class DefaultPostRepository @Inject constructor(
    private val postService: PostService,
) : PostRepository {

    override suspend fun getPosts(eventId: Long): ApiResponse<List<Post>> {
        return postService
            .getPosts(eventId)
            .map { it.toData() }
    }

    override suspend fun uploadPost(
        eventId: Long,
        title: String,
        content: String,
        imageUrls: List<String>,
    ): ApiResponse<Long> {
        val imageFiles = imageUrls.map { imageUrl ->
            val file = File(imageUrl)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                IMAGES_KEY,
                file.name,
                requestFile,
            )
        }

        val postDataFiles = HashMap<String, RequestBody>()
        postDataFiles[EVENT_ID_KEY] =
            eventId.toString().toRequestBody("application/json".toMediaTypeOrNull())
        postDataFiles[TITLE_KEY] = title.toRequestBody("application/json".toMediaTypeOrNull())
        postDataFiles[CONTENT_KEY] = content.toRequestBody("application/json".toMediaTypeOrNull())
        return postService.uploadPost(postDataFiles, imageFiles)
    }

    companion object {
        const val EVENT_ID_KEY = "eventId"
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "content"
        const val IMAGES_KEY = "images"
    }
}
