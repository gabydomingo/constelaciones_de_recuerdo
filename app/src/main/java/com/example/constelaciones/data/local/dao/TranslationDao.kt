package com.example.constelaciones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.constelaciones.data.local.entity.TranslationEntity

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations WHERE originalText = :original LIMIT 1")
    suspend fun getTranslation(original: String): TranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity)

    // (puedes conservar tu getAllTranslations si lo usas en otra parte)
}
