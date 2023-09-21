package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.common.imagePrefixUrl
import com.emmsale.data.model.FeedDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun FeedDetailResponse.toData(): FeedDetail = FeedDetail(
    id = id,
    eventId = eventId,
    title = title,
    content = content,
    writer = writer.toData(),
    imageUrls = imageUrls.map { imagePrefixUrl + it },
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
