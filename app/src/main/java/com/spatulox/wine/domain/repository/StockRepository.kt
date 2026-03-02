package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Stock

interface StockRepository {
    suspend fun getStock(): List<Stock>
    suspend fun getStockById(id: Int): Stock?
    suspend fun insert(stock: Stock): Long
    suspend fun withdraw(stock: Stock, reason: String): Long
    suspend fun withdraw(stockId: Int, reason: String): Long
    suspend fun delete(stock: Stock)
    suspend fun deleteById(id: Int)
}