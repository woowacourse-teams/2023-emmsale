package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDescriptionUpdateRequestBody(
    @SerialName("description")
    val description: String,
)
