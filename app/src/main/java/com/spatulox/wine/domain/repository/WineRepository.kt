package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Wine
import kotlinx.coroutines.flow.Flow

interface WineRepository {
    suspend fun getWineById(id: Int): Wine?
    fun getWineStream(): Flow<List<Wine>>
    fun getwineYearsStream(): Flow<List<Int>>
    suspend fun insert(wine: Wine): Long
    suspend fun delete(wine: Wine)
    suspend fun update(wine: Wine): Int
}
