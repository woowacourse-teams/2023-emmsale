package com.emmsale.data.eventTag.mapper

import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.remote.dto.EventTagApiModel

fun EventTagApiModel.toData(): EventTag = EventTag(
    id = id,
    name = name,
)

fun List<EventTagApiModel>.toData(): List<EventTag> = map { it.toData() }
