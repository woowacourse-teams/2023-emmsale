package com.emmsale.model

data class BlockedMember(
    val blockedMemberId: Long,
    val memberName: String,
    val blockId: Long,
    val profileImageUrl: String,
)
