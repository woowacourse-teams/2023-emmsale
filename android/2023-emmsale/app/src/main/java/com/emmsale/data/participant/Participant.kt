package com.emmsale.data.participant

data class Participant(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val description: String?,
)
