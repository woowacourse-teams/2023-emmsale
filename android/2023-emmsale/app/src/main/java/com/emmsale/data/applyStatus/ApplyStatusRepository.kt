package com.emmsale.data.applyStatus

interface ApplyStatusRepository {

    suspend fun getApplyStatuses(): List<ApplyStatus>

    suspend fun getApplyStatusesByIds(ids: List<Long>): List<ApplyStatus>
}
