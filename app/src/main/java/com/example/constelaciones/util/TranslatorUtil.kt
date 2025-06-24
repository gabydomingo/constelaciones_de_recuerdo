// src/main/java/com/example/constelaciones/util/TranslatorUtil.kt
package com.example.constelaciones.util

import android.util.Log
import com.example.constelaciones.data.local.dao.TranslationDao
import com.example.constelaciones.data.local.entity.TranslationEntity
import com.example.constelaciones.data.remote.LibreTranslateClient
import com.example.constelaciones.data.remote.MyMemoryTranslateClient
import com.example.constelaciones.data.remote.TranslateRequest

object TranslatorUtil {
    private lateinit var dao: TranslationDao

    /** Llamar una sola vez, en Application.onCreate() */
    fun init(translationDao: TranslationDao) {
        dao = translationDao
    }

    /**
     * Traduce `original` consultando primero Room y, si no existe, pide a la API.
     * Loggea "traduccion tomada de room" o "traduccion traida de api" según corresponda.
     */
    suspend fun translateText(
        original: String,
        email: String = "domingogaby8@gmail.com"
    ): String {
        // 1) Intentar desde ROOM
        dao.getTranslation(original)?.let { entity ->
            Log.d("TranslatorUtil", "traduccion tomada de room: \"${original.take(30)}…\"")
            return entity.translatedText
        }

        // 2) No estaba en Room → traída de API
        Log.d("TranslatorUtil", "traduccion traida de api: \"${original.take(30)}…\"")

        // 2a) Intento con MyMemory
        try {
            val mem = MyMemoryTranslateClient.api.translate(
                text = original, langPair = "en|es", email = email, apiKey = "4baef49ce12c2609e0a2"
            )
            if (mem.responseStatus == 200) {
                val translated = mem.responseData.translatedText
                dao.insertTranslation(
                    TranslationEntity(
                        originalText = original,
                        translatedText = translated
                    )
                )
                return translated
            }
        } catch (_: Exception) { /* ignoro para fallback */ }

        // 2b) Fallback a LibreTranslate
        return try {
            val lib = LibreTranslateClient.api.translate(TranslateRequest(q = original))
            val translated = lib.translatedText
            dao.insertTranslation(
                TranslationEntity(
                    originalText = original,
                    translatedText = translated
                )
            )
            translated
        } catch (_: Exception) {
            // Si todo falla, devuelvo el original
            original
        }
    }
}
