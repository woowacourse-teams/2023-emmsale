package com.emmsale.data.resumeTag

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi

class ResumeTagRepositoryImpl(
    private val resumeTagService: ResumeTagService = FakeResumeTagService()
) : ResumeTagRepository {

    override suspend fun getEducationTags(): ApiResult<List<ResumeTag>> {
        return when (val educationsResponse = handleApi { resumeTagService.getEducationTags() }) {
            is ApiSuccess -> ApiSuccess(ResumeTag.from(educationsResponse.data))
            is ApiError -> ApiError(educationsResponse.code, educationsResponse.message)
            is ApiException -> ApiException(educationsResponse.e)
        }
    }

    override suspend fun getConferenceTags(): ApiResult<List<ResumeTag>> {
        return when (val conferencesResponse = handleApi { resumeTagService.getConferenceTags() }) {
            is ApiSuccess -> ApiSuccess(ResumeTag.from(conferencesResponse.data))
            is ApiError -> ApiError(conferencesResponse.code, conferencesResponse.message)
            is ApiException -> ApiException(conferencesResponse.e)
        }
    }
}
