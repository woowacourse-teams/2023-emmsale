package com.emmsale.data.mapper

import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.apiModel.response.FeedResponse
import com.emmsale.data.model.Feed
import com.emmsale.data.model.FeedDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JvmName("FeedDetailResponse")
fun FeedDetailResponse.toData(): FeedDetail = FeedDetail(
    id = id,
    eventId = eventId,
    title = title,
    content = content,
    writer = writer.toData(),
    imageUrls = imageUrls.map { BuildConfig.IMAGE_URL_PREFIX + it },
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

fun List<FeedResponse>.toData(): List<Feed> = map { it.toData() }

fun FeedResponse.toData(): Feed = Feed(
    id = id,
    eventId = eventId,
    title = title,
    content = content,
    writer = writer.toData(),
    imageUrls = imageUrls.map { BuildConfig.IMAGE_URL_PREFIX + it },
    commentCount = commentCount,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
