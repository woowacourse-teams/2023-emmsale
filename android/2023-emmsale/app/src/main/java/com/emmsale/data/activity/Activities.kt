package com.emmsale.data.activity

data class Activities(
    val category: String,
    val activities: List<Activity>
)

data class Activity(
    val id: Int,
    val name: String,
)
