package com.emmsale.data.network.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageUrlResponse(
    @SerialName("imageUrl")
    val profileImageUrl: String,
)
