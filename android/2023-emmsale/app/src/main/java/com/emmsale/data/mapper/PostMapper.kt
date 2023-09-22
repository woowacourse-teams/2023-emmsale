package com.emmsale.data.mapper

import android.util.Log
import com.emmsale.BuildConfig
import com.emmsale.data.apiModel.response.PostsResponse
import com.emmsale.data.model.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun PostsResponse.toData(): List<Post> {
    return posts.map { postResponse ->
        Post(
            id = postResponse.id,
            eventId = eventId,
            title = postResponse.title,
            content = postResponse.content,
            titleImageUrl = getTitleImageUrl(postResponse.imageUrls),
            createdAt = LocalDateTime.parse(postResponse.createdAt, dateTimeFormatter),
            updatedAt = LocalDateTime.parse(postResponse.updatedAt, dateTimeFormatter),
            commentCount = postResponse.commentCount,
        ).apply { Log.d("wooseok", getTitleImageUrl(postResponse.imageUrls) ?: "") }
    }
}

private fun getTitleImageUrl(images: List<String>): String? {
    if (images.isEmpty()) return null
    return BuildConfig.IMAGE_URL_PREFIX + images.firstOrNull()
}

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
