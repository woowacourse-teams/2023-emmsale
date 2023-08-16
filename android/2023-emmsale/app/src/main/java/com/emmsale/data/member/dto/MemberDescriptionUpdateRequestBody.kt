package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDescriptionUpdateRequestBody(
    @SerialName("description")
    val description: String,
)
