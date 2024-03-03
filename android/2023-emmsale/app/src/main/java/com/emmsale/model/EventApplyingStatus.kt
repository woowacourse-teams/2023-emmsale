package com.emmsale.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed interface EventApplyingStatus {
    data class UpComing(val daysUntilStart: Int) : EventApplyingStatus
    data class InProgress(val daysUntilDeadline: Int) : EventApplyingStatus
    object Ended : EventApplyingStatus

    companion object {
        fun create(
            applyingStartDate: LocalDateTime,
            applyingEndDate: LocalDateTime,
        ): EventApplyingStatus {
            val nowDateTime = LocalDateTime.now()
            return when {
                nowDateTime.isBefore(applyingStartDate) -> UpComing(
                    ChronoUnit.DAYS.between(
                        nowDateTime.toLocalDate(),
                        applyingStartDate.toLocalDate(),
                    ).toInt(),
                )

                nowDateTime.isAfter(applyingEndDate) -> Ended
                else -> InProgress(
                    ChronoUnit.DAYS.between(
                        nowDateTime.toLocalDate(),
                        applyingEndDate.toLocalDate(),
                    ).toInt(),
                )
            }
        }
    }
}
