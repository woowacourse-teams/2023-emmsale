package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.FeedResponse
import com.emmsale.data.model.Event
import com.emmsale.data.model.Feed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun List<FeedResponse>.toData(): List<Feed> = map { it.toData() }

fun FeedResponse.toData(): Feed = Feed(
    id = id,
    event = Event(
        id = eventId,
        name = eventName,
    ),
    title = title,
    content = content,
    writer = writer.toData(),
    imageUrls = imageUrls.map { BuildConfig.IMAGE_URL_PREFIX + it },
    commentCount = commentCount,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
