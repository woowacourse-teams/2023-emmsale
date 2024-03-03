package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.EventTagResponse
import com.emmsale.model.EventTag

fun EventTagResponse.toData(): EventTag = EventTag(
    id = id,
    name = name,
)

fun List<EventTagResponse>.toData(): List<EventTag> = map { it.toData() }
