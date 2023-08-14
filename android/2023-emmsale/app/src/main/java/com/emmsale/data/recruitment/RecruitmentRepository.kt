package com.emmsale.data.recruitment

import com.emmsale.data.common.ApiResult

interface RecruitmentRepository {

    suspend fun fetchEventRecruitments(
        eventId: Long,
    ): ApiResult<List<Recruitment>>

    suspend fun saveRecruitment(eventId: Long): ApiResult<Unit>
    suspend fun deleteRecruitment(eventId: Long): ApiResult<Unit>

    suspend fun requestCompanion(
        eventId: Long,
        memberId: Long,
        message: String,
    ): ApiResult<Unit>

    suspend fun checkParticipationStatus(eventId: Long): ApiResult<Boolean>
}