package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import java.io.File

interface PostRepository {

    suspend fun uploadPost(
        eventId: Long,
        title: String,
        content: String,
        imageFiles: List<File>,
    ): ApiResponse<Long>
}
