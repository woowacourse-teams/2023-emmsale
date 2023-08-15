package com.emmsale.data.member

data class Member(
    val id: Long,
    val githubId: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val openProfileUrl: String,
)
