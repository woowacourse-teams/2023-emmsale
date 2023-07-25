package com.emmsale.data.career

import android.util.Log
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CareerRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val careerService: CareerService,
) : CareerRepository {

    override suspend fun getCareers(): ApiResult<List<Career>> = withContext(dispatcher) {
        when (val careersApiResult = handleApi { careerService.getCareers() }) {
            is ApiSuccess -> ApiSuccess(Career.from(careersApiResult.data))
            is ApiError -> {
                ApiError(careersApiResult.code, careersApiResult.message)
            }
            is ApiException -> {
                Log.d("buna", careersApiResult.e.toString())
                ApiException(careersApiResult.e)
            }
        }
    }
}
