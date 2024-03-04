package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.ActivityResponse
import com.emmsale.model.Activity
import com.emmsale.model.ActivityType

fun List<ActivityResponse>.toData(): List<Activity> = map { it.toData() }

fun ActivityResponse.toData(): Activity = Activity(
    id = id,
    activityType = activityType.toData(),
    name = name,
)

private fun ActivityResponse.ActivityType.toData(): ActivityType = when (this) {
    ActivityResponse.ActivityType.CLUB -> ActivityType.CLUB
    ActivityResponse.ActivityType.EDUCATION -> ActivityType.EDUCATION
    ActivityResponse.ActivityType.INTEREST_FIELD -> ActivityType.INTEREST_FIELD
}
