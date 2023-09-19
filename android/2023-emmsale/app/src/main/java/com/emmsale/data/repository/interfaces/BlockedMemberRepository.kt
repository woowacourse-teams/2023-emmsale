package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.BlockedMember

interface BlockedMemberRepository {

    suspend fun getBlockedMembers(): ApiResponse<List<BlockedMember>>

    suspend fun deleteBlockedMember(blockId: Long): ApiResponse<Unit>
}
