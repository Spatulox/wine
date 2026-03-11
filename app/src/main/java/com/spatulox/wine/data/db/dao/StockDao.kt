package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.spatulox.wine.data.db.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stock ORDER BY date DESC")
    suspend fun getStock(): List<StockEntity>

    @Query("SELECT * FROM stock WHERE compartmentId= :compartmentId AND `shelfId`= :shelfId AND col= :col")
    suspend fun getStockByPos(compartmentId: Int, shelfId: Int, col: Int): StockEntity?

    @Query("SELECT * FROM stock WHERE id= :id")
    suspend fun getStockById(id: Int): StockEntity?

    @Query("SELECT * FROM stock ORDER BY compartmentId, shelfId, col")
    fun getStockStream(): Flow<List<StockEntity>>

    @Query("""
        SELECT DISTINCT w.year 
        FROM wine w
        INNER JOIN stock s ON w.id = s.wineId
        ORDER BY w.year ASC
    """)
    fun getStockYearsStream(): Flow<List<Int>>

    @Insert
    suspend fun insert(stock: StockEntity): Long

    @Update
    suspend fun update(stock: StockEntity)

    @Delete
    suspend fun delete(stock: StockEntity)

    @Query("DELETE FROM stock WHERE id= :id")
    suspend fun delete(id: Int)
}