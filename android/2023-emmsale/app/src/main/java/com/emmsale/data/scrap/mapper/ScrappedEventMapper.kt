package com.emmsale.data.scrap.mapper

import com.emmsale.data.scrap.ScrappedEvent
import com.emmsale.data.scrap.dto.ScrappedEventApiModel

fun ScrappedEventApiModel.toData(): ScrappedEvent = ScrappedEvent(
    scrapId = scrapId,
    eventId = eventId,
    name = name,
    status = status,
    imageUrl = imageUrl,
    tags = tags,
)

fun List<ScrappedEventApiModel>.toData(): List<ScrappedEvent> = map {
    it.toData()
}
