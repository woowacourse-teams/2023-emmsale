package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.EventTagApiModel
import com.emmsale.data.model.EventTag

fun EventTagApiModel.toData(): EventTag = EventTag(
    id = id,
    name = name,
)

fun List<EventTagApiModel>.toData(): List<EventTag> = map { it.toData() }
