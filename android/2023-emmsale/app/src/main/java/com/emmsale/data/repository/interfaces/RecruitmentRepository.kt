package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Recruitment

interface RecruitmentRepository {

    suspend fun getEventRecruitments(
        eventId: Long,
    ): ApiResponse<List<Recruitment>>

    suspend fun getEventRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResponse<Recruitment>

    suspend fun postRecruitment(
        eventId: Long,
        content: String,
    ): ApiResponse<Long>

    suspend fun editRecruitment(
        eventId: Long,
        recruitmentId: Long,
        content: String,
    ): ApiResponse<Unit>

    suspend fun deleteRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResponse<Unit>

    suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResponse<Unit>

    suspend fun checkIsAlreadyRequestCompanion(
        eventId: Long,
        senderId: Long,
        receiverId: Long,
    ): ApiResponse<Boolean>

    suspend fun checkIsAlreadyPostRecruitment(eventId: Long): ApiResponse<Boolean>

    suspend fun reportRecruitment(
        recruitmentId: Long,
        authorId: Long,
        reporterId: Long,
    ): ApiResponse<Unit>
}