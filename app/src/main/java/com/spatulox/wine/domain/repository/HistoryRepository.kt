package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.History

interface HistoryRepository {
    suspend fun getHistory(): List<History>
    suspend fun getHistoryById(id: Int): History
    suspend fun insert(history: History): Long
}