package com.emmsale.data.career

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi

class CareerRepositoryImpl(
    private val careerService: CareerService
) : CareerRepository {

    override suspend fun getCareers(): ApiResult<List<Career>> {
        return when (val careersApiResult = handleApi { careerService.getCareers() }) {
            is ApiSuccess -> ApiSuccess(Career.from(careersApiResult.data))
            is ApiError -> ApiError(careersApiResult.code, careersApiResult.message)
            is ApiException -> ApiException(careersApiResult.e)
        }
    }
}
