package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.FeedDetailResponse
import com.emmsale.data.model.FeedDetail
import java.time.LocalDateTime

fun FeedDetailResponse.toData(): FeedDetail = FeedDetail(
    id = id,
    eventId = eventId,
    title = title,
    content = content,
    writer = writer.toData(),
    imageUrls = imageUrls,
    createdAt = LocalDateTime.parse(createdAt),
    updatedAt = LocalDateTime.parse(updatedAt),
)
