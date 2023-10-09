package com.emmsale.data.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed interface EventApplicationStatus {
    data class UpComing(val remainingDays: Int) : EventApplicationStatus
    data class InProgress(val remainingDays: Int) : EventApplicationStatus
    object Ended : EventApplicationStatus

    companion object {
        fun create(
            applicationStartDate: LocalDateTime,
            applicationEndDate: LocalDateTime,
        ): EventApplicationStatus {
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
