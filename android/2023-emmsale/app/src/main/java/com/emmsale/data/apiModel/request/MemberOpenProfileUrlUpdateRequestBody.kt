package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberOpenProfileUrlUpdateRequestBody(
    @SerialName("openProfileUrl")
    val openProfileUrl: String,
)
