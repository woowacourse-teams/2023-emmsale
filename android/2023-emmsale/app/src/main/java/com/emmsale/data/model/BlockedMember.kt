package com.emmsale.data.model

data class BlockedMember(
    val blockedMemberId: Long,
    val memberName: String,
    val blockId: Long,
    val profileImageUrl: String,
)
