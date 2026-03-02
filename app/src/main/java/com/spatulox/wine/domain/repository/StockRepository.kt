package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getStock(): List<Stock>
    suspend fun getStockById(id: Int): Stock?

    fun getStockStream(): Flow<List<Stock>>
    suspend fun insert(stock: Stock, reason: String): Long
    suspend fun withdraw(stock: Stock, reason: String): Long
    suspend fun withdraw(stockId: Int, reason: String): Long
    suspend fun delete(stock: Stock)
    suspend fun deleteById(id: Int)
}