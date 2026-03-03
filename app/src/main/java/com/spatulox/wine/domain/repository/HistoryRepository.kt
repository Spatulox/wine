package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun getHistory(): List<History>
    suspend fun getHistoryById(id: Int): History?
    fun getHistoryStream(): Flow<List<History>>
    suspend fun insert(history: History): Long
}