package com.emmsale.data.activity

import com.emmsale.data.activity.dto.ActivitiesApiModel
import com.emmsale.data.activity.dto.ActivityApiModel

data class Activities(
    val category: String,
    val activities: List<Activity>
) {
    companion object {
        fun from(activitiesApiModels: List<ActivitiesApiModel>): List<Activities> =
            activitiesApiModels.map(::from)

        private fun from(activitiesApiModel: ActivitiesApiModel): Activities = Activities(
            category = activitiesApiModel.category,
            activities = Activity.from(activitiesApiModel.activities)
        )
    }
}

data class Activity(
    val id: Int,
    val name: String,
) {
    companion object {
        fun from(activitiesApiModel: List<ActivityApiModel>): List<Activity> =
            activitiesApiModel.map(::from)

        private fun from(activityApiModel: ActivityApiModel): Activity =
            Activity(
                id = activityApiModel.id,
                name = activityApiModel.name
            )
    }
}
