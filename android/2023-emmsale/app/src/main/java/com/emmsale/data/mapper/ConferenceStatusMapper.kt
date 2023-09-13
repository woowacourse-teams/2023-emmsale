package com.emmsale.data.mapper

import com.emmsale.data.model.ConferenceStatus

fun List<ConferenceStatus>.toApiModel(): List<String> = map { it.toApiModel() }

private fun ConferenceStatus.toApiModel(): String = when (this) {
    ConferenceStatus.IN_PROGRESS -> "IN_PROGRESS"
    ConferenceStatus.SCHEDULED -> "UPCOMING"
    ConferenceStatus.ENDED -> "ENDED"
}
