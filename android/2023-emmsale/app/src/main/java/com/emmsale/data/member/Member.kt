package com.emmsale.data.member

data class Member(
    val id: Long,
    val githubUrl: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val openProfileUrl: String,
)
