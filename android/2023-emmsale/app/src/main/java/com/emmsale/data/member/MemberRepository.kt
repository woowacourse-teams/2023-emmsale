package com.emmsale.data.member

import com.emmsale.data.common.ApiResult

interface MemberRepository {

    suspend fun fetchMember(memberId: Long): ApiResult<Member1>

    suspend fun updateMember(member: Member): ApiResult<Unit>
}
