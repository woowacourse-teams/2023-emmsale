package com.emmsale.data.mapper

import com.emmsale.data.common.database.entity.EventSearchEntity
import com.emmsale.data.model.EventSearch

fun EventSearch.toEntity(): EventSearchEntity = EventSearchEntity(
    id = id,
    query = query,
    createdAt = createdAt,
)

fun EventSearchEntity.toData(): EventSearch = EventSearch(
    id = id,
    query = query,
    createdAt = createdAt,
)
