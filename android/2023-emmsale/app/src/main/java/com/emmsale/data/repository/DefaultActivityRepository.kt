package com.emmsale.data.repository

import com.emmsale.data.apiModel.response.ActivitiesApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Activity
import com.emmsale.data.service.ActivityService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultActivityRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val activityService: ActivityService,
) : ActivityRepository {

    override suspend fun getActivities(): ApiResult<List<Activity>> = withContext(dispatcher) {
        handleApi(
            execute = { activityService.getActivities() },
            mapToDomain = List<ActivitiesApiModel>::toData,
        )
    }

    override suspend fun getActivities(memberId: Long): ApiResult<List<Activity>> =
        withContext(dispatcher) {
            handleApi(
                execute = { activityService.getActivities(memberId) },
                mapToDomain = { it.toData() },
            )
        }
}
