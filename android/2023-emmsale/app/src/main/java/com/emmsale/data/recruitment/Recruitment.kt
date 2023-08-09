package com.emmsale.data.recruitment

data class Recruitment(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val description: String?,
)
