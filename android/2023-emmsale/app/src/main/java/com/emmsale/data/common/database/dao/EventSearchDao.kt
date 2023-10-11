package com.emmsale.data.common.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.emmsale.data.common.database.entity.EventSearchEntity

@Dao
interface EventSearchDao {
    @Query("SELECT * FROM event_search ORDER BY created_at DESC")
    fun getAll(): List<EventSearchEntity>

    @Insert
    fun save(eventSearch: EventSearchEntity)

    @Delete
    fun delete(eventSearch: EventSearchEntity)

    @Query("DELETE FROM event_search")
    fun deleteAll()
}
