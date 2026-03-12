package com.spatulox.wine.domain.repository

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.Flow

// Stock represent a single position in a shelf
interface StockRepository {
    suspend fun getStock(): List<StockWithWine>
    suspend fun getStockById(id: Int): StockWithWine?
    suspend fun getStockByPos(pos: Position): StockWithWine?
    suspend fun getStockByShelfId(id: Int): StockWithWine?
    suspend fun getStockByCompartmentId(id: Int): StockWithWine?
    fun getStockStream(): Flow<List<StockWithWine>>
    fun getStockYearsStream(): Flow<List<Int>>
    suspend fun insert(stock: StockWithWine, reason: String): Long
    suspend fun update(stock: StockWithWine)
    suspend fun withdraw(stock: StockWithWine, reason: String)
    suspend fun withdraw(stockId: Int, reason: String)
    suspend fun delete(stock: StockWithWine)
    suspend fun delete(pos: Position)
    suspend fun delete(stockId: Int)
}