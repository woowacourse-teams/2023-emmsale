package com.emmsale.data.recruitment

import com.emmsale.data.common.ApiResult

interface RecruitmentRepository {

    suspend fun fetchEventRecruitments(
        eventId: Long,
    ): ApiResult<List<Recruitment>>

    suspend fun postRecruitment(
        eventId: Long,
        content: String,
    ): ApiResult<Unit>

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

    suspend fun checkParticipationStatus(eventId: Long): ApiResult<Boolean>
}
