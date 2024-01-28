package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.RecruitmentResponse
import com.emmsale.data.model.Event
import com.emmsale.data.model.Recruitment

fun List<RecruitmentResponse>.toData(): List<Recruitment> = map {
    it.toData()
}

fun RecruitmentResponse.toData(): Recruitment = Recruitment(
    id = id,
    content = content,
    updatedDate = updatedAt,
    writer = member.toData(),
    event = Event(id = eventId),
)
