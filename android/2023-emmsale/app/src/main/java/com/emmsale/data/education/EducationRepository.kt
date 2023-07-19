package com.emmsale.data.education

import com.emmsale.data.common.ApiResult

interface EducationRepository {
    suspend fun fetchEducations(): ApiResult<List<Education>>
}
