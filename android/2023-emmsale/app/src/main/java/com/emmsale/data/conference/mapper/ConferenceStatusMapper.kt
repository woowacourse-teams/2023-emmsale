package com.emmsale.data.conference.mapper

import com.emmsale.data.conference.ConferenceStatus

fun String.toData(): ConferenceStatus = when (this) {
    "진행 중" -> ConferenceStatus.IN_PROGRESS
    "진행 예정" -> ConferenceStatus.SCHEDULED
    "종료된 행사" -> ConferenceStatus.ENDED
    else -> throw IllegalArgumentException("Unknown conference status: $this")
}

fun List<ConferenceStatus>.toApiModel(): List<String> = map { it.toApiModel() }

private fun ConferenceStatus.toApiModel(): String = when (this) {
    ConferenceStatus.IN_PROGRESS -> "IN_PROGRESS"
    ConferenceStatus.SCHEDULED -> "UPCOMING"
    ConferenceStatus.ENDED -> "ENDED"
}
