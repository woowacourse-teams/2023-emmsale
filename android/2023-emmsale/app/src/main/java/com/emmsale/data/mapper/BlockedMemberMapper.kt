package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.BlockedMemberResponse
import com.emmsale.data.model.BlockedMember

fun List<BlockedMemberResponse>.toData(): List<BlockedMember> = map(BlockedMemberResponse::toData)

fun BlockedMemberResponse.toData(): BlockedMember = BlockedMember(
    blockedMemberId = blockedMemberId,
    memberName = memberName,
    blockId = blockId,
    profileImageUrl = profileImageUrl,
)
