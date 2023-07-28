package com.emmsale.data.activity

import com.emmsale.data.common.ApiResult

interface ActivityRepository {
    suspend fun getActivities(): ApiResult<List<Activities>>
}
