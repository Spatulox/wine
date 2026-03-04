package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spatulox.wine.data.db.entity.ShelfEntity
import com.spatulox.wine.data.db.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @Query("SELECT * FROM shelf")
    fun getAllShelves(): Flow<List<ShelfEntity>>

    @Query("""
    SELECT * FROM stock 
    WHERE shelfId = :shelfId 
    ORDER BY `row`, col
""")
    fun getStockByShelf(shelfId: Int): Flow<List<StockEntity>>


}