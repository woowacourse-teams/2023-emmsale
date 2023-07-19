package com.emmsale.data.education

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi

class EducationRepositoryImpl(
    private val educationService: EducationService = FakeEducationService()
) : EducationRepository {

    override suspend fun fetchEducations(): ApiResult<List<Education>> {
        return when (val educationsResponse = handleApi { educationService.getEducations() }) {
            is ApiSuccess -> ApiSuccess(Education.from(educationsResponse.data))
            is ApiError -> ApiError(educationsResponse.code, educationsResponse.message)
            is ApiException -> ApiException(educationsResponse.e)
        }
    }
}
