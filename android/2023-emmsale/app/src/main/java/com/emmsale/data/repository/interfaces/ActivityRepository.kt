package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Activity

interface ActivityRepository {

    suspend fun getActivities(): ApiResponse<List<Activity>>

    suspend fun getActivities(memberId: Long): ApiResponse<List<Activity>>
}
