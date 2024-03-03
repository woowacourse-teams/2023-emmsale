package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.response.ActivityResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.mapper.toData
import com.emmsale.model.Activity
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.network.service.ActivityService
import com.emmsale.data.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultActivityRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val activityService: ActivityService,
) : ActivityRepository {
    private var allActivities: List<Activity> = emptyList()

    override suspend fun getActivities(): ApiResponse<List<Activity>> = withContext(dispatcher) {
        if (allActivities.isNotEmpty()) return@withContext Success(allActivities)
        val result = activityService
            .getActivities()
            .map(List<ActivityResponse>::toData)

        if (result is Success) {
            allActivities = result.data
            return@withContext result
        }
        return@withContext result
    }

    override suspend fun getActivities(
        memberId: Long,
    ): ApiResponse<List<Activity>> = withContext(dispatcher) {
        activityService
            .getActivities(memberId)
            .map { it.toData() }
    }
}
