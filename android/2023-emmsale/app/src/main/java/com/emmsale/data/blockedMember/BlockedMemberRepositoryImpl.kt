package com.emmsale.data.blockedMember

import com.emmsale.data.blockedMember.dto.BlockedMemberApiModel
import com.emmsale.data.blockedMember.mapper.toData
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlockedMemberRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val blockedMemberService: BlockedMemberService,
) : BlockedMemberRepository {

    override suspend fun getBlockedMembers(): ApiResult<List<BlockedMember>> =
        withContext(dispatcher) {
            handleApi(
                execute = { blockedMemberService.getBlockedMembers() },
                mapToDomain = { blockedMembers -> blockedMembers.map(BlockedMemberApiModel::toData) },
            )
        }

    override suspend fun deleteBlockedMember(blockId: Long): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = { blockedMemberService.deleteBlockedMember(blockId) },
                mapToDomain = { },
            )
        }
}