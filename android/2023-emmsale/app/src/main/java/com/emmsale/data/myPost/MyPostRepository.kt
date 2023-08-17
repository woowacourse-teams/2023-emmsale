package com.emmsale.data.myPost

import com.emmsale.data.common.ApiResult

interface MyPostRepository {
    suspend fun getMyPosts(): ApiResult<List<MyPost>>
}
