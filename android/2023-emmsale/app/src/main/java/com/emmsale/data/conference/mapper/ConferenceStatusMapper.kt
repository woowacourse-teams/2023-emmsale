package com.emmsale.data.conference.mapper

import com.emmsale.data.conferenceStatus.ConferenceStatus

fun String.toData(): ConferenceStatus = when (this) {
    "IN_PROGRESS" -> ConferenceStatus.IN_PROGRESS
    "UPCOMING" -> ConferenceStatus.SCHEDULED
    "ENDED" -> ConferenceStatus.ENDED
    else -> throw IllegalArgumentException("Unknown conference status: $this")
}

fun List<ConferenceStatus>.toApiModel(): List<String> = map { it.toApiModel() }

private fun ConferenceStatus.toApiModel(): String = when (this) {
    ConferenceStatus.IN_PROGRESS -> "IN_PROGRESS"
    ConferenceStatus.SCHEDULED -> "UPCOMING"
    ConferenceStatus.ENDED -> "ENDED"
}
