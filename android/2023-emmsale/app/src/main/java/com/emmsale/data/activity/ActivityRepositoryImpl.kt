package com.emmsale.data.activity

import android.util.Log
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivityRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val activityService: ActivityService,
) : ActivityRepository {

    override suspend fun getActivities(): ApiResult<List<Activities>> = withContext(dispatcher) {
        when (val activitiesApiResult = handleApi { activityService.getActivities() }) {
            is ApiSuccess -> ApiSuccess(Activities.from(activitiesApiResult.data))
            is ApiError -> ApiError(activitiesApiResult.code, activitiesApiResult.message)
            is ApiException -> {
                Log.d("buna", activitiesApiResult.e.toString())
                ApiException(activitiesApiResult.e)
            }
        }
    }
}
