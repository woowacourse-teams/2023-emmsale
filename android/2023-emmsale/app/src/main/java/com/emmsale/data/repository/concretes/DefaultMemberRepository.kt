package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.MemberActivitiesUpdateRequest
import com.emmsale.data.apiModel.request.MemberBlockCreateRequest
import com.emmsale.data.apiModel.request.MemberCreateRequest
import com.emmsale.data.apiModel.request.MemberDescriptionUpdateRequest
import com.emmsale.data.apiModel.response.MemberResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.service.MemberService
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val memberService: MemberService,
) : MemberRepository {

    override suspend fun getMember(
        memberId: Long,
    ): ApiResponse<Member> = withContext(dispatcher) {
        memberService
            .getMember(memberId)
            .map(MemberResponse::toData)
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
        profileImageUrl: String,
    ): ApiResponse<String> = withContext(dispatcher) {
        val file = File(profileImageUrl)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val formDataFile = MultipartBody.Part.createFormData(
            IMAGE_KEY,
            file.name,
            requestFile,
        )

        memberService.updateMemberProfileImage(
            memberId = memberId,
            profileImageFile = formDataFile,
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
