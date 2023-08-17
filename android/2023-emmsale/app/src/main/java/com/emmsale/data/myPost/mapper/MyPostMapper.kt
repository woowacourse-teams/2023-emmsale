package com.emmsale.data.myPost.mapper

import com.emmsale.data.myPost.MyPost
import com.emmsale.data.myPost.dto.MyPostApiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<MyPostApiModel>.toData(): List<MyPost> = map { it.toData() }
fun MyPostApiModel.toData(): MyPost {
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
