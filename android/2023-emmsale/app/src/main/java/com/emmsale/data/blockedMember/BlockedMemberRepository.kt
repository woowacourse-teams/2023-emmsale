package com.emmsale.data.blockedMember

import com.emmsale.data.common.ApiResult

interface BlockedMemberRepository {
    suspend fun getBlockedMembers(): ApiResult<List<BlockedMember>>
}