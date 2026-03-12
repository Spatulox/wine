package com.spatulox.wine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spatulox.wine.data.db.dao.CompartmentDao
import com.spatulox.wine.data.db.dao.ShelfDao
import com.spatulox.wine.data.db.dao.StockDao
import com.spatulox.wine.data.db.dao.WineDao
import com.spatulox.wine.data.db.entity.CompartmentEntity
import com.spatulox.wine.data.db.entity.ShelfEntity
import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.data.db.entity.WineEntity

@Database(
    entities = [WineEntity::class, StockEntity::class, ShelfEntity::class, CompartmentEntity::class],
    version = 24
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wineDao(): WineDao
    abstract fun compartmentDao(): CompartmentDao
    abstract fun stockDao(): StockDao
    abstract fun shelfDao(): ShelfDao
}