package com.emmsale.data.repository.concretes

import com.emmsale.data.mapper.toData
import com.emmsale.data.network.apiModel.response.BlockedMemberResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.network.di.IoDispatcher
import com.emmsale.data.network.service.BlockedMemberService
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.model.BlockedMember
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultBlockedMemberRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
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
