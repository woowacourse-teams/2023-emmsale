package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.MemberActivitiesUpdateRequest
import com.emmsale.data.apiModel.request.MemberBlockCreateRequest
import com.emmsale.data.apiModel.request.MemberCreateRequest
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequest
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Activity
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.service.ActivityService
import com.emmsale.data.service.MemberService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val memberService: MemberService,
    private val activityService: ActivityService,
) : MemberRepository {

    override suspend fun getMember(
        memberId: Long,
    ): ApiResponse<Member> = withContext(dispatcher) {
        val deferredActivities = async {
            activityService.getActivities(memberId).map { it.toData() }
        }

        val deferredMember = async {
            memberService.getMember(memberId).map { it.toData() }
        }

        when (val memberApiResponse = deferredMember.await()) {
            is Success -> getMemberFromActivities(
                memberApiResponse.data,
                deferredActivities.await(),
            )

            else -> memberApiResponse
        }
    }

    private fun getMemberFromActivities(
        member: Member,
        activitiesApiResponse: ApiResponse<List<Activity>>,
    ): ApiResponse<Member> {
        return when (activitiesApiResponse) {
            is Success -> Success(member.copy(activities = activitiesApiResponse.data))
            is Failure -> Failure(activitiesApiResponse.code, activitiesApiResponse.message)
            NetworkError -> NetworkError
            is Unexpected -> Unexpected(activitiesApiResponse.error)
        }
    }

    override suspend fun createMember(
        name: String,
        activityIds: List<Long>,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService.updateMember(
            MemberCreateRequest(
                name = name,
                activityIds = activityIds,
            ),
        )
    }

    override suspend fun updateMemberDescription(
        description: String,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService.updateMemberDescription(
            MemberDescriptionUpdateRequest(description),
        )
    }

    override suspend fun updateMemberProfileImage(
        memberId: Long,
        profileImageFile: File,
    ): ApiResponse<String> = withContext(dispatcher) {
        val requestBody = profileImageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val formData = MultipartBody.Part.createFormData(
            IMAGE_KEY,
            profileImageFile.name,
            requestBody,
        )

        memberService.updateMemberProfileImage(
            memberId = memberId,
            formDatas = formData,
        ).map { it.profileImageUrl }
    }

    override suspend fun addMemberActivities(
        activityIds: List<Long>,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService.addMemberActivities(
            MemberActivitiesUpdateRequest(activityIds),
        )
    }

    override suspend fun deleteMemberActivities(
        activityIds: List<Long>,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService
            .deleteMemberActivities(activityIds)
            .map { }
    }

    override suspend fun deleteMember(
        memberId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService.deleteMember(memberId)
    }

    override suspend fun blockMember(
        memberId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        memberService.blockMember(
            MemberBlockCreateRequest(memberId),
        )
    }

    companion object {
        private const val IMAGE_KEY = "image"
    }
}
