// src/main/java/com/example/constelaciones/data/local/database/AppDatabase.kt
package com.example.constelaciones.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.constelaciones.data.local.dao.TranslationDao
import com.example.constelaciones.data.local.entity.TranslationEntity

@Database(
    entities = [TranslationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
}
