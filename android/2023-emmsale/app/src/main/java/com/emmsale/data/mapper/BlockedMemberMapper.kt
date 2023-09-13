package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.BlockedMemberApiModel
import com.emmsale.data.model.BlockedMember

fun BlockedMemberApiModel.toData(): BlockedMember = BlockedMember(
    id = id,
    memberName = memberName,
    blockId = blockId,
    profileImageUrl = profileImageUrl,
)
