package com.emmsale.data.scrap.mapper

import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.scrap.ScrappedEvent
import com.emmsale.data.scrap.dto.ScrappedEventApiModel

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
