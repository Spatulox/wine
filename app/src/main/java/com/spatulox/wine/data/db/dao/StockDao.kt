package com.spatulox.wine.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.spatulox.wine.data.db.entity.StockEntity

interface StockDao {
    @Query("SELECT * FROM stock ORDER BY date DESC")
    suspend fun getStock(): List<StockEntity>

    @Query("SELECT * FROM stock WHERE id= :id")
    suspend fun getStockById(id: Int): StockEntity?

    @Insert
    suspend fun insert(stock: StockEntity): Long

    @Delete
    suspend fun delete(stock: StockEntity)

    @Query("DELETE FROM stock WHERE id= :id")
    suspend fun delete(id: Int)
}