package com.emmsale.data.member

import com.emmsale.data.activity.Activity1
import com.emmsale.data.activity.ActivityType

data class Member1(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val activities: Map<ActivityType, List<Activity1>>,
) {
    fun getActivities(activityType: ActivityType): List<Activity1> =
        activities[activityType] ?: listOf()
}
