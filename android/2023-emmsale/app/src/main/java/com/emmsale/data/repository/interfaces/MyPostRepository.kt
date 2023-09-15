package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.MyPost

interface MyPostRepository {

    suspend fun getMyPosts(): ApiResponse<List<MyPost>>
}
