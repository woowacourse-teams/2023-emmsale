package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.Member
import java.io.File

interface MemberRepository {

    suspend fun getMember(memberId: Long): ApiResponse<Member>

    suspend fun createMember(name: String, activityIds: List<Long>): ApiResponse<Unit>

    suspend fun updateMemberDescription(description: String): ApiResponse<Unit>

    suspend fun updateMemberProfileImage(
        memberId: Long,
        profileImageFile: File,
    ): ApiResponse<String>

    suspend fun addMemberActivities(activityIds: List<Long>): ApiResponse<Unit>

    suspend fun deleteMemberActivities(activityIds: List<Long>): ApiResponse<Unit>

    suspend fun deleteMember(memberId: Long): ApiResponse<Unit>

    suspend fun blockMember(memberId: Long): ApiResponse<Unit>
}
