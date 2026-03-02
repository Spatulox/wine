package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.HistoryDao
import com.spatulox.wine.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val historyDao: HistoryDao): HistoryRepository {
}