package com.emmsale.data.repository

import com.emmsale.data.apiModel.request.MemberActivitiesUpdateRequest
import com.emmsale.data.apiModel.request.MemberCreateRequest
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequest
import com.emmsale.data.apiModel.request.MemberOpenProfileUrlUpdateRequest
import com.emmsale.data.apiModel.response.BlockRequestBody
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Member
import com.emmsale.data.service.MemberService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultMemberRepository(
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
                    MemberCreateRequest(
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
                        MemberDescriptionUpdateRequest(description),
                    )
                },
                mapToDomain = {},
            )
        }

    override suspend fun updateMemberOpenProfileUrl(openProfileUrl: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = {
                    memberService.updateMemberOpenProfileUrl(
                        MemberOpenProfileUrlUpdateRequest(openProfileUrl),
                    )
                },
                mapToDomain = {},
            )
        }

    override suspend fun addMemberActivities(activityIds: List<Long>): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = {
                    memberService.addMemberActivities(MemberActivitiesUpdateRequest(activityIds))
                },
                mapToDomain = {},
            )
        }

    override suspend fun deleteMemberActivities(activityIds: List<Long>): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = { memberService.deleteMemberActivities(activityIds) },
                mapToDomain = { },
            )
        }

    override suspend fun deleteMember(memberId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { memberService.deleteMember(memberId) },
            mapToDomain = {},
        )
    }

    override suspend fun blockMember(memberId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { memberService.blockMember(BlockRequestBody(memberId)) },
            mapToDomain = {},
        )
    }
}
