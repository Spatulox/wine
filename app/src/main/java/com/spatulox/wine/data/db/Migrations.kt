package com.spatulox.wine.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v26: comment on the wine. SQLite needs a DEFAULT to add a NOT NULL column to existing rows
val MIGRATION_25_26 = object : Migration(25, 26) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `wine` ADD COLUMN `comment` TEXT NOT NULL DEFAULT ''")
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_25_26)
