package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.BlockedMember

interface BlockedMemberRepository {
    suspend fun getBlockedMembers(): ApiResult<List<BlockedMember>>

    suspend fun deleteBlockedMember(blockId: Long): ApiResult<Unit>
}
