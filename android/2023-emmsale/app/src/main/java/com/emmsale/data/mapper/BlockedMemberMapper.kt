package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.BlockedMemberResponse
import com.emmsale.data.model.BlockedMember

fun BlockedMemberResponse.toData(): BlockedMember = BlockedMember(
    id = id,
    memberName = memberName,
    blockId = blockId,
    profileImageUrl = profileImageUrl,
)
