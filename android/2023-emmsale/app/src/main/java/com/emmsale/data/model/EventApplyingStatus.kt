package com.emmsale.data.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed interface EventApplyingStatus {
    data class UpComing(val remainingDays: Int) : EventApplyingStatus
    data class InProgress(val remainingDays: Int) : EventApplyingStatus
    object Ended : EventApplyingStatus

    companion object {
        fun create(
            applicationStartDate: LocalDateTime,
            applicationEndDate: LocalDateTime,
        ): EventApplyingStatus {
            val nowDateTime = LocalDateTime.now()
            return when {
                nowDateTime.isBefore(applicationStartDate) -> UpComing(
                    ChronoUnit.DAYS.between(
                        nowDateTime.toLocalDate(),
                        applicationStartDate.toLocalDate(),
                    ).toInt(),
                )

                nowDateTime.isAfter(applicationEndDate) -> Ended
                else -> InProgress(
                    ChronoUnit.DAYS.between(
                        nowDateTime.toLocalDate(),
                        applicationEndDate.toLocalDate(),
                    ).toInt(),
                )
            }
        }
    }
}
