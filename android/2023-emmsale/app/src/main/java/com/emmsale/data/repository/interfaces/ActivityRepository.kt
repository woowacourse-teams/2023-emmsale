package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.Activity

interface ActivityRepository {

    suspend fun getActivities(): ApiResponse<List<Activity>>

    suspend fun getActivities(memberId: Long): ApiResponse<List<Activity>>
}
