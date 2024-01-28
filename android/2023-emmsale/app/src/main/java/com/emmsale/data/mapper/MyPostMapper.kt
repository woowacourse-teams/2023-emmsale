package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.model.Event
import com.emmsale.data.model.Member
import com.emmsale.data.model.Recruitment

fun List<MyPostResponse>.toData(): List<Recruitment> = map { it.toData() }
fun MyPostResponse.toData() = Recruitment(
    id = postId,
    writer = Member(),
    event = Event(id = eventId, name = eventName),
    content = content,
    updatedDate = updatedAt,
)
