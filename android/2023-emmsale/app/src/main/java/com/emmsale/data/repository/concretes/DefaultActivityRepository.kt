package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.ActivitiesResponse
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Activity
import com.emmsale.data.repository.interfaces.ActivityRepository
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
            mapToDomain = List<ActivitiesResponse>::toData,
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
