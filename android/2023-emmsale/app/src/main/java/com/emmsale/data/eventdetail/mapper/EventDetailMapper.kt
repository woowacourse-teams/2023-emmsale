package com.emmsale.data.eventdetail.mapper

import com.emmsale.data.eventdetail.EventDetail
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun EventDetailApiModel.toData(): EventDetail = EventDetail(
    id = id,
    name = name,
    status = status,
    location = location,
    startDate = startDate.toLocalDateTime(),
    endDate = endDate.toLocalDateTime(),
    informationUrl = informationUrl,
    tags = tags,
    imageUrl = imageUrl,
    remainingDays = remainingDays,
    type = type,
)

private fun String.toLocalDateTime(): LocalDateTime {
    val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, format)
}
