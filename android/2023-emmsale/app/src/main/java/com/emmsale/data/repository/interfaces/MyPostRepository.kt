package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.MyPost

interface MyPostRepository {
    suspend fun getMyPosts(): ApiResult<List<MyPost>>
}
