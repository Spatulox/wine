package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.Flow

// Stock represent a single position in a shelf
interface StockRepository {
    suspend fun getStockByPos(pos: Position): StockWithWine?
    suspend fun hasStockInShelf(shelfId: Int): Boolean
    suspend fun hasStockInCompartment(compartmentId: Int): Boolean
    fun getStockStream(): Flow<List<StockWithWine>>
    fun getStockYearsStream(): Flow<List<Int>>
    suspend fun insert(stock: StockWithWine): Long
    suspend fun update(stock: StockWithWine)
    suspend fun move(stock: StockWithWine, to: Position)
    suspend fun withdraw(stock: StockWithWine)
    suspend fun delete(pos: Position)
}