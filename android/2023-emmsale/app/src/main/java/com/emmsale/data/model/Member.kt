package com.emmsale.data.model

data class Member(
    val id: Long,
    val githubUrl: String,
    val name: String,
    val description: String,
    val profileImageUrl: String,
    val activities: List<Activity> = emptyList(),
)
