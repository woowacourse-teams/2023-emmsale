package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.Member

interface MemberRepository {

    suspend fun getMember(memberId: Long): ApiResponse<Member>

    suspend fun createMember(name: String, activityIds: List<Long>): ApiResponse<Unit>

    suspend fun updateMemberDescription(description: String): ApiResponse<Unit>

    suspend fun updateMemberProfileImage(
        memberId: Long,
        profileImageUrl: String,
    ): ApiResponse<String>

    suspend fun addMemberActivities(activityIds: List<Long>): ApiResponse<Unit>

    suspend fun deleteMemberActivities(activityIds: List<Long>): ApiResponse<Unit>

    suspend fun deleteMember(memberId: Long): ApiResponse<Unit>

    suspend fun blockMember(memberId: Long): ApiResponse<Unit>
}
