package com.emmsale.data.mapper

import com.emmsale.data.common.database.entity.EventSearchHistoryEntity
import com.emmsale.data.model.EventSearchHistory

fun EventSearchHistory.toEntity(): EventSearchHistoryEntity = EventSearchHistoryEntity(
    query = query,
    createdAt = createdAt,
).also { it.id = id }

fun EventSearchHistoryEntity.toData(): EventSearchHistory = EventSearchHistory(
    id = id,
    query = query,
    createdAt = createdAt,
)
