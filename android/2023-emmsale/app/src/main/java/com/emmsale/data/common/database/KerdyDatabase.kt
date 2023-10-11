package com.emmsale.data.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emmsale.data.common.database.dao.EventSearchHistoryDao
import com.emmsale.data.common.database.entity.EventSearchHistoryEntity

@Database(
    entities = [EventSearchHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class KerdyDatabase : RoomDatabase() {
    abstract fun eventDao(): EventSearchHistoryDao

    companion object {
        private const val DATABASE_NAME = "kerdy_database"

        private var instance: KerdyDatabase? = null

        @Synchronized
        fun getInstance(context: Context): KerdyDatabase = instance ?: synchronized(this) {
            instance ?: createKerdyDatabase(context).also { instance = it }
        }

        private fun createKerdyDatabase(context: Context): KerdyDatabase {
            return Room.databaseBuilder(context, KerdyDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
