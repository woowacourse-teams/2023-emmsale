package com.emmsale.data.blockedMember.mapper

import com.emmsale.data.blockedMember.BlockedMember
import com.emmsale.data.blockedMember.dto.BlockedMemberApiModel

fun BlockedMemberApiModel.toData(): BlockedMember = BlockedMember(
    id = id,
    memberName = memberName,
    blockId = blockId,
    profileImageUrl = profileImageUrl,
)
