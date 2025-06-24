// data/local/entity/TranslationEntity.kt
package com.example.constelaciones.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val translatedText: String,
    val timestamp: Long = System.currentTimeMillis()
)
