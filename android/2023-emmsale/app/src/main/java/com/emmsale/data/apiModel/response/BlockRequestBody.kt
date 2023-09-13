package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockRequestBody(
    @SerialName("blockMemberId")
    val blockMemberId: Long,
)
