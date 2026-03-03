package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.HistoryDao
import com.spatulox.wine.data.db.entity.HistoryEntityWithWine
import com.spatulox.wine.data.mapper.HistoryMapper
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.domain.model.HistoryWithWine
import com.spatulox.wine.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDao: HistoryDao): HistoryRepository {
    override suspend fun getHistory(): List<History> {
        return historyDao.getHistory().map { HistoryMapper.toDomain(it) }
    }

    override fun getHistoryStream(): Flow<List<History>> {
        return historyDao.getHistoryStream().map { entities -> entities.map { HistoryMapper.toDomain(it) } }
    }

    override fun getHistoryWithWineStream(): Flow<List<HistoryWithWine>> {
        return historyDao.getHistoryWithWineStream().map { entities -> entities.map { HistoryMapper.toDomainWithWine(it) } }
    }

    override suspend fun getHistoryWithWineById(id: Int): HistoryWithWine? {
        val entity = historyDao.getHistoryWithWineById(id)
        return entity?.let { HistoryMapper.toDomainWithWine(it) }
    }

    override suspend fun getHistoryById(id: Int): History? {
        val entity = historyDao.getHistoryById(id)
        return entity?.let { HistoryMapper.toDomain(it) }
    }

    override suspend fun insert(history: History): Long {
        val entity = HistoryMapper.toEntity(history)
        historyDao.insert(entity)
        return entity.id.toLong()
    }
}