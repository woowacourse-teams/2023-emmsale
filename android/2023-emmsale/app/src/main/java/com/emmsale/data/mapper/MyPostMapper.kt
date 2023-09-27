package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.model.MyRecruitmentPost
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun List<MyPostResponse>.toData(): List<MyRecruitmentPost> = map { it.toData() }
fun MyPostResponse.toData(): MyRecruitmentPost {
    return MyRecruitmentPost(
        eventId = eventId,
        postId = postId,
        eventName = eventName,
        content = content,
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate = LocalDate.parse(this, formatter)
    return localDate.atTime(LocalTime.of(0, 0))
}
