package com.emmsale.data.model

sealed class EventStatus {
    object IN_PROGRESS : EventStatus()
    data class UPCOMING(val remainingDays: Int) : EventStatus()
    object ENDED : EventStatus()
}
