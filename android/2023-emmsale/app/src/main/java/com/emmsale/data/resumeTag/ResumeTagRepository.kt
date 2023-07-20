package com.emmsale.data.resumeTag

import com.emmsale.data.common.ApiResult

interface ResumeTagRepository {
    suspend fun getEducationTags(): ApiResult<List<ResumeTag>>
}
