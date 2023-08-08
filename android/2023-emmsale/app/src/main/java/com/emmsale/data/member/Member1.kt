package com.emmsale.data.member

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType

data class Member1(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val activities: List<Activity>,
) {
    fun getActivities(activityType: ActivityType): List<Activity> =
        activities.filter { it.activityType == activityType }
}
