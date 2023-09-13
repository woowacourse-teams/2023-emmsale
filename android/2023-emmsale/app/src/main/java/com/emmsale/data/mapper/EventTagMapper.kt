package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.model.EventTag

fun EventTagResponse.toData(): EventTag = EventTag(
    id = id,
    name = name,
)

fun List<EventTagResponse>.toData(): List<EventTag> = map { it.toData() }
