package com.emmsale.data.member

import com.emmsale.data.common.ApiResult

interface MemberRepository {

    suspend fun getMember(memberId: Long): ApiResult<Member>

    suspend fun updateMember(name: String, activityIds: List<Long>): ApiResult<Unit>

    suspend fun updateMemberDescription(description: String): ApiResult<Unit>

    suspend fun updateMemberOpenProfileUrl(openProfileUrl: String): ApiResult<Unit>

    suspend fun addMemberActivities(activityIds: List<Long>): ApiResult<Unit>

    suspend fun deleteMemberActivities(activityIds: List<Long>): ApiResult<Unit>

    suspend fun deleteMember(memberId: Long): ApiResult<Unit>

    suspend fun blockMember(memberId: Long): ApiResult<Unit>
}
