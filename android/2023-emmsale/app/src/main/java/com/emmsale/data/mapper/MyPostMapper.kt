package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.model.Event
import com.emmsale.data.model.Member
import com.emmsale.data.model.MyPost
import com.emmsale.data.model.Recruitment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<MyPostResponse>.toData(): List<Recruitment> = map { it.toData() }
fun MyPostResponse.toData() = Recruitment(
    id = postId,
    writer = Member(),
    event = Event(id = eventId, name = eventName),
    content = content,
    updatedDate = updatedAt.toLocalDate(),
)

fun List<MyPostResponse>.toMyPosts(): List<MyPost> = map { it.toMyPost() }
fun MyPostResponse.toMyPost(): MyPost {
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
