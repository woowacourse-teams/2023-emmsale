package com.emmsale.data.network.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockedMemberResponse(
    @SerialName("blockMemberId")
    val blockedMemberId: Long,
    @SerialName("id")
    val blockId: Long,
    @SerialName("memberName")
    val memberName: String,
    @SerialName("imageUrl")
    val profileImageUrl: String,
)
