package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Wine

interface WineRepository {
    suspend fun getWine(): List<Wine>
    suspend fun getWineById(id: Int): Wine?
    suspend fun getWinesByYear(year: Int): List<Wine>
    suspend fun searchWines(query: String): List<Wine>
    suspend fun insert(wine: Wine): Long
    suspend fun delete(wineId: Int)
    suspend fun delete(wine: Wine)
    suspend fun update(wine: Wine): Int
}
