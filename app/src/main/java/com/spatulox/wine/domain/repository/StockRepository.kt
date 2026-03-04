package com.spatulox.wine.domain.repository

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import kotlinx.coroutines.flow.Flow

// Stock represent a single position in a shelf
interface StockRepository {
    suspend fun getStock(): List<Stock>
    suspend fun getStockById(id: Int): Stock?
    suspend fun getStockByPos(pos: Position): Stock?
    fun getStockStream(): Flow<List<Stock>>
    fun getStockYearsStream(): Flow<List<Int>>
    suspend fun insert(stock: Stock, reason: String): Long
    suspend fun withdraw(stock: Stock, reason: String): Long
    suspend fun withdraw(stockId: Int, reason: String): Long
    suspend fun delete(stock: Stock)
    suspend fun delete(pos: Position)
    suspend fun delete(stockId: Int)
}