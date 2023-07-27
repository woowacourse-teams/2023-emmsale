package com.emmsale.data.activity

import com.emmsale.data.activity.dto.ActivitiesApiModel
import com.emmsale.data.activity.dto.toData
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivityRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val activityService: ActivityService,
) : ActivityRepository {

    override suspend fun getActivities(): ApiResult<List<Activities>> = withContext(dispatcher) {
        handleApi(activityService.getActivities(), List<ActivitiesApiModel>::toData)
    }
}
