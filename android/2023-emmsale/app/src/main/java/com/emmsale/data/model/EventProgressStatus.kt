package com.emmsale.data.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed interface EventProgressStatus {
    data class UpComing(val remainingDays: Int) : EventProgressStatus
    object InProgress : EventProgressStatus
    object Ended : EventProgressStatus

    companion object {
        fun create(startDate: LocalDateTime, endDate: LocalDateTime): EventProgressStatus {
            val nowDateTime = LocalDateTime.now()
            return when {
                nowDateTime.isBefore(startDate) -> UpComing(
                    ChronoUnit.DAYS.between(nowDateTime, startDate).toInt(),
                )

                nowDateTime.isAfter(endDate) -> Ended
                else -> InProgress
            }
        }
    }
}
