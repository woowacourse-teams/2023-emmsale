package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.model.MyPost
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<MyPostResponse>.toData(): List<MyPost> = map { it.toData() }
fun MyPostResponse.toData(): MyPost {
    return MyPost(
        postId = postId,
        eventId = eventId,
        eventName = eventName,
        content = content,
        updatedAt = updatedAt.toLocalDate(),
    )
}

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}
