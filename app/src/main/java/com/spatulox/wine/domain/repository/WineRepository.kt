package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine
import kotlinx.coroutines.flow.Flow

interface WineRepository {
    suspend fun getWine(): List<Wine>
    suspend fun getWineByPos(pos: Position): Wine?

    suspend fun getWineById(id: Int): Wine?
    suspend fun getWinesByYear(year: Int): List<Wine>
    fun getWineStream(): Flow<List<Wine>>
    fun getwineYearsStream(): Flow<List<Int>>
    suspend fun searchWines(query: String): List<Wine>
    suspend fun insert(wine: Wine): Long
    suspend fun withdrawWine(wine: Wine)
    suspend fun delete(wineId: Int)
    suspend fun delete(wine: Wine)
    suspend fun update(wine: Wine): Int
}
