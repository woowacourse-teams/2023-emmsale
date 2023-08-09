package com.emmsale.data.member

import com.emmsale.data.common.ApiResult

interface MemberRepository {

    suspend fun getMember(memberId: Long): ApiResult<Member>

    suspend fun updateMember(name: String, activityIds: List<Long>): ApiResult<Unit>
}
