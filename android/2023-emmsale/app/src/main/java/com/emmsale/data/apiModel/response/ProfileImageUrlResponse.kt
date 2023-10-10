package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageUrlResponse(
    @SerialName("imageUrl")
    val imageUrl: String,
)
