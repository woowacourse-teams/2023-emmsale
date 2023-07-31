package com.emmsale.data.member.dto

import kotlinx.serialization.Serializable

@Serializable
data class MemberWithoutActivitiesApiModel(
    val id: Long,
    val name: String = "",
    val description: String = "",
    val imageUrl: String
)
