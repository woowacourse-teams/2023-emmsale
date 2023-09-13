package com.emmsale.data.repository

import com.emmsale.data.apiModel.response.BlockedMemberApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.BlockedMember
import com.emmsale.data.service.BlockedMemberService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultBlockedMemberRepository(
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
