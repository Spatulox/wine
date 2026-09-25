package com.spatulox.wine.data.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            // No destructive fallback: a schema change without a Migration must fail loudly
            // instead of silently wiping the user's cellar.
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "wine_db"
            )
                .addMigrations(*ALL_MIGRATIONS)
                .build()
                .also { INSTANCE = it }
        }
    }
}
