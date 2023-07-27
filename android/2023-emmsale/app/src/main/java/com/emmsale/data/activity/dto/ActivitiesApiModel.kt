package com.emmsale.data.activity.dto

import com.emmsale.data.activity.Activities
import com.emmsale.data.activity.Activity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivitiesApiModel(
    @SerialName("activityType")
    val category: String = "-",
    @SerialName("activityResponses")
    val activities: List<ActivityApiModel> = emptyList()
) {
    fun toData(): Activities = Activities(
        category = category,
        activities.map { it.toData() }
    )
}

fun List<ActivitiesApiModel>.toData(): List<Activities> = map { it.toData() }

@Serializable
data class ActivityApiModel(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
) {
    fun toData(): Activity = Activity(
        id = id,
        name = name
    )
}
