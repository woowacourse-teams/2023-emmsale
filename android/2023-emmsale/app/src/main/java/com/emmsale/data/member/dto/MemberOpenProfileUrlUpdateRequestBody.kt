package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberOpenProfileUrlUpdateRequestBody(
    @SerialName("openProfileUrl")
    val openProfileUrl: String,
)
