package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ActivityResponse
import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType

fun List<ActivityResponse>.toData(): List<Activity> = map { it.toData() }

fun ActivityResponse.toData(): Activity = Activity(
    id = id,
    activityType = activityType.toData(),
    name = name,
)

private fun ActivityResponse.ActivityType.toData(): ActivityType = when (this) {
    ActivityResponse.ActivityType.CLUB -> ActivityType.CLUB
    ActivityResponse.ActivityType.EDUCATION -> ActivityType.EDUCATION
    ActivityResponse.ActivityType.FIELD -> ActivityType.FIELD
}
