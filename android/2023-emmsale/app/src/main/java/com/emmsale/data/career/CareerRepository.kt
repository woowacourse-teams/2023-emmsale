package com.emmsale.data.career

import com.emmsale.data.common.ApiResult

interface CareerRepository {
    suspend fun getCareers(): ApiResult<List<Career>>
}
