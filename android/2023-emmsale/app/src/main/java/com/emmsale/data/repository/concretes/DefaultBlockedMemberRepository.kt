package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.BlockedMemberResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.BlockedMember
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.service.BlockedMemberService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultBlockedMemberRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val blockedMemberService: BlockedMemberService,
) : BlockedMemberRepository {

    override suspend fun getBlockedMembers(): ApiResponse<List<BlockedMember>> =
        withContext(dispatcher) {
            blockedMemberService
                .getBlockedMembers()
                .map(List<BlockedMemberResponse>::toData)
        }

    override suspend fun deleteBlockedMember(
        blockId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        blockedMemberService.deleteBlockedMember(blockId)
    }
}
