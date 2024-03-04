package com.emmsale.data.persistence.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventSearchHistoryDao {
    @Query("SELECT * FROM event_search ORDER BY created_at DESC")
    fun getAll(): List<EventSearchHistoryEntity>

    @Insert
    fun save(eventSearch: EventSearchHistoryEntity)

    @Delete
    fun delete(eventSearch: EventSearchHistoryEntity)

    @Query("DELETE FROM event_search WHERE `query` = :query")
    fun deleteByQuery(query: String)

    @Query("DELETE FROM event_search")
    fun deleteAll()
}
