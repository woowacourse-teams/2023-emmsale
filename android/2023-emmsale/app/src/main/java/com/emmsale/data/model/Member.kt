package com.emmsale.data.model

data class Member(
    val id: Long = DEFAULT_ID,
    val githubUrl: String = "",
    val name: String = "",
    val description: String = "",
    val profileImageUrl: String = "",
    val activities: List<Activity> = emptyList(),
) {
    companion object {
        private const val DEFAULT_ID = -1L
    }
}
