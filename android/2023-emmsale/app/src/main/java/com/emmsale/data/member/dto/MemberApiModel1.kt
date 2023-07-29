package com.emmsale.data.member.dto

import kotlinx.serialization.Serializable

@Serializable
data class MemberApiModel1(
    val id: Long,
    val name: String,
    val description: String = "",
    val imageUrl: String
)
