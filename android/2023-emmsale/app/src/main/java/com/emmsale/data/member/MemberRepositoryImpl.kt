package com.emmsale.data.member

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.member.dto.MemberApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val memberService: MemberService,
) : MemberRepository {
    override suspend fun updateMember(member: Member): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(memberService.updateMember(MemberApiModel.from(member))) { }
    }
}
