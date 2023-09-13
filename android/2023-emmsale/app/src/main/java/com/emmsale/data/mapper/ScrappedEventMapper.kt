package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ScrappedEventApiModel
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.ScrappedEvent

fun ScrappedEventApiModel.toData(): ScrappedEvent {
    return ScrappedEvent(
        scrapId = scrapId,
        eventId = eventId,
        name = name,
        status = status.toConferenceStatusData(),
        imageUrl = imageUrl,
        tags = tags,
    )
}

fun List<ScrappedEventApiModel>.toData(): List<ScrappedEvent> = map {
    it.toData()
}

private fun String.toConferenceStatusData(): ConferenceStatus = when (this) {
    "IN_PROGRESS" -> ConferenceStatus.IN_PROGRESS
    "UPCOMING" -> ConferenceStatus.SCHEDULED
    "ENDED" -> ConferenceStatus.ENDED
    else -> throw IllegalArgumentException("Unknown conference status: $this")
}
