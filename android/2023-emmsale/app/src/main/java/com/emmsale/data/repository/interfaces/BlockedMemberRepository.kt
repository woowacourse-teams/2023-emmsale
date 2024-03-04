package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.BlockedMember

interface BlockedMemberRepository {

    suspend fun getBlockedMembers(): ApiResponse<List<BlockedMember>>

    suspend fun deleteBlockedMember(blockId: Long): ApiResponse<Unit>
}
