package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.Activity

interface ActivityRepository {
    suspend fun getActivities(): ApiResult<List<Activity>>

    suspend fun getActivities(memberId: Long): ApiResult<List<Activity>>
}
