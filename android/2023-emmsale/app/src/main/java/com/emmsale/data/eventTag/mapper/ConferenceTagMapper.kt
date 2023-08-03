package com.emmsale.data.eventTag.mapper

import com.emmsale.data.eventTag.ConferenceTag
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.dto.ConferenceTagApiModel

fun ConferenceTagApiModel.toData(): EventTag = ConferenceTag(
    id = id,
    name = name,
)

fun List<ConferenceTagApiModel>.toData(): List<EventTag> = map { it.toData() }

fun ConferenceTag.toApiModel(): ConferenceTagApiModel = ConferenceTagApiModel(
    id = id,
    name = name,
)
