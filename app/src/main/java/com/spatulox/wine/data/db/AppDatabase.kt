package com.spatulox.wine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spatulox.wine.data.db.dao.HistoryDao
import com.spatulox.wine.data.db.dao.StockDao
import com.spatulox.wine.data.db.dao.WineDao
import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.data.db.entity.WineEntity

@Database(
    entities = [HistoryEntity::class, WineEntity::class, StockEntity::class],
    version = 9
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun wineDao(): WineDao

    abstract fun stockDao(): StockDao
}