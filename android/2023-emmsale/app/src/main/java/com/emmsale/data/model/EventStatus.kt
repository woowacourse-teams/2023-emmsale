package com.emmsale.data.model

sealed class EventStatus {
    object InProgress : EventStatus()
    data class Upcoming(val remainingDays: Int) : EventStatus()
    object Ended : EventStatus()
}
