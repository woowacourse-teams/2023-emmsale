package com.emmsale.data.resumeTag

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi

class ResumeTagRepositoryImpl(
    private val educationTagService: EducationTagService = FakeEducationTagService()
) : ResumeTagRepository {

    override suspend fun getEducationTags(): ApiResult<List<ResumeTag>> {
        return when (val educationsResponse = handleApi { educationTagService.getEducationTags() }) {
            is ApiSuccess -> ApiSuccess(ResumeTag.from(educationsResponse.data))
            is ApiError -> ApiError(educationsResponse.code, educationsResponse.message)
            is ApiException -> ApiException(educationsResponse.e)
        }
    }
}
