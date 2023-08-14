package com.emmsale.data.member

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.member.dto.MemberDescriptionUpdateRequestBody
import com.emmsale.data.member.dto.MemberUpdateRequestBody
import com.emmsale.data.member.mapper.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val memberService: MemberService,
) : MemberRepository {

    override suspend fun getMember(memberId: Long): ApiResult<Member> = withContext(dispatcher) {
        handleApi(
            execute = { memberService.getMember(memberId) },
            mapToDomain = { it.toData() },
        )
    }

    override suspend fun updateMember(
        name: String,
        activityIds: List<Long>,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                memberService.updateMember(
                    MemberUpdateRequestBody(
                        name = name,
                        activityIds = activityIds,
                    ),
                )
            },
            mapToDomain = { },
        )
    }

    override suspend fun updateMemberDescription(description: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = {
                    memberService.updateMemberDescription(
                        MemberDescriptionUpdateRequestBody(description),
                    )
                },
                mapToDomain = {},
            )
        }

    override suspend fun updateMemberActivities(activities: List<Long>): ApiResult<Unit> {
        return ApiSuccess(Unit)
    }

    override suspend fun deleteMember(memberId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { memberService.deleteMember(memberId) },
            mapToDomain = {},
        )
    }
}
