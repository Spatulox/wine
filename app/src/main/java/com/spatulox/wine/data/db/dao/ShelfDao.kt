package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Insert
    suspend fun insert(shelf: ShelfEntity): Long

    @Update
    suspend fun update(shelf: ShelfEntity): Int

    @Delete
    suspend fun delete(shelf: ShelfEntity)

    @Query("DELETE FROM shelf WHERE id= :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM shelf ORDER BY id ASC")
    fun getShelfStream(): Flow<List<ShelfEntity>>

}