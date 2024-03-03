package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.RecruitmentResponse
import com.emmsale.model.Event
import com.emmsale.model.Recruitment

fun List<RecruitmentResponse>.toData(): List<Recruitment> = map {
    it.toData()
}

fun RecruitmentResponse.toData(): Recruitment = Recruitment(
    id = id,
    content = content,
    updatedDate = updatedAt,
    writer = member.toData(),
    event = Event(
        id = eventId,
        name = eventName,
    ),
)
