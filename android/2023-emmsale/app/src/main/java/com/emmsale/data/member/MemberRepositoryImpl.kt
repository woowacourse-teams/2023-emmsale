package com.emmsale.data.member

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.member.dto.MemberApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class MemberRepositoryImpl(
    private val externalScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val memberService: MemberService,
) : MemberRepository {

    override suspend fun updateMember(member: Member): ApiResult<Unit> =
        withContext(externalScope.coroutineContext + dispatcher) {
            when (val updateResponse =
                handleApi { memberService.updateMember(MemberApiModel.from(member)) }) {
                is ApiSuccess -> ApiSuccess(Unit)
                is ApiError -> ApiError(updateResponse.code, updateResponse.message)
                is ApiException -> ApiException(updateResponse.e)
            }
        }
}

