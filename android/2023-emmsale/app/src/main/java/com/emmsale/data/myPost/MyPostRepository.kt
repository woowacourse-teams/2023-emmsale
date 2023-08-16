package com.emmsale.data.myPost

import com.emmsale.data.common.ApiResult

interface MyPostRepository {
    fun getMyPosts(): ApiResult<List<MyPost>>
}
