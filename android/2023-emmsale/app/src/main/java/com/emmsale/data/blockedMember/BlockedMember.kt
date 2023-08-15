package com.emmsale.data.blockedMember

data class BlockedMember(
    val id: Long,
    val memberName: String,
    val blockId: Long,
    val profileImageUrl: String,
)
