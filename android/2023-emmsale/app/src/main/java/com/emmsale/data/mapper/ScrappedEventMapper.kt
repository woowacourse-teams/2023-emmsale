package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ScrappedEventResponse
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.ScrappedEvent

fun ScrappedEventResponse.toData(): ScrappedEvent {
    return ScrappedEvent(
        scrapId = scrapId,
        eventId = eventId,
        name = name,
        status = status.toConferenceStatusData(),
        imageUrl = imageUrl,
        tags = tags,
    )
}

fun List<ScrappedEventResponse>.toData(): List<ScrappedEvent> = map {
    it.toData()
}

private fun String.toConferenceStatusData(): ConferenceStatus = when (this) {
    "IN_PROGRESS" -> ConferenceStatus.IN_PROGRESS
    "UPCOMING" -> ConferenceStatus.UPCOMING
    "ENDED" -> ConferenceStatus.ENDED
    else -> throw IllegalArgumentException("Unknown conference status: $this")
}
