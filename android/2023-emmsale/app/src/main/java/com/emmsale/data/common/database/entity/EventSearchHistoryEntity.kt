package com.emmsale.data.common.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_search")
data class EventSearchHistoryEntity(
    @ColumnInfo("query") val query: String,
    @ColumnInfo("created_at") val createdAt: Long = System.currentTimeMillis(),
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}
