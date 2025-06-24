package com.example.constelaciones

import android.app.Application
import androidx.room.Room
import com.example.constelaciones.data.local.database.AppDatabase
import com.example.constelaciones.util.TranslatorUtil

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "constelaciones-db"
        ).build()
        TranslatorUtil.init(db.translationDao())
    }
}
