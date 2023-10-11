package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.BlockedMemberResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.BlockedMember
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.service.BlockedMemberService
import com.emmsale.di.modules.other.IoDispatcher
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
