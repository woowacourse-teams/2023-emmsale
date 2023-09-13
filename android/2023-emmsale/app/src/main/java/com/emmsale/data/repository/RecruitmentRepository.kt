package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.Recruitment

interface RecruitmentRepository {

    suspend fun getEventRecruitments(
        eventId: Long,
    ): ApiResult<List<Recruitment>>

    suspend fun getEventRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResult<Recruitment>

    suspend fun postRecruitment(
        eventId: Long,
        content: String,
    ): ApiResult<Long>

    suspend fun editRecruitment(
        eventId: Long,
        recruitmentId: Long,
        content: String,
    ): ApiResult<Unit>

    suspend fun deleteRecruitment(
        eventId: Long,
        recruitmentId: Long,
    ): ApiResult<Unit>

    suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResult<Unit>

    suspend fun checkIsAlreadyRequestCompanion(
        eventId: Long,
        senderId: Long,
        receiverId: Long,
    ): ApiResult<Boolean>

    suspend fun checkIsAlreadyPostRecruitment(eventId: Long): ApiResult<Boolean>

    suspend fun reportRecruitment(
        recruitmentId: Long,
        authorId: Long,
        reporterId: Long,
    ): ApiResult<Unit>
}
