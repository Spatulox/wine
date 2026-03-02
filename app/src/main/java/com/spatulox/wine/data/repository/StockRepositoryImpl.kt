package com.spatulox.wine.data.repository

import androidx.room.withTransaction
import com.spatulox.wine.data.db.AppDatabase
import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.db.dao.HistoryDao
import com.spatulox.wine.data.db.dao.StockDao
import com.spatulox.wine.data.mapper.HistoryMapper
import com.spatulox.wine.domain.repository.StockRepository

import com.spatulox.wine.data.mapper.StockMapper
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.domain.model.Stock

class StockRepositoryImpl(
    private val stockDao: StockDao,
    private val historyDao: HistoryDao,
    private val transactionProvider: TransactionProvider
) : StockRepository {

    override suspend fun getStock(): List<Stock> {
        return stockDao.getStock().map { StockMapper.toDomain(it) }
    }

    override suspend fun getStockById(id: Int): Stock? {
        val entity = stockDao.getStockById(id)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override suspend fun insert(stock: Stock): Long {
        val entity = StockMapper.toEntity(stock)
        return stockDao.insert(entity)
    }

    override suspend fun withdraw(stock: Stock, reason: String): Long {
        return transactionProvider.run {
            val history = HistoryMapper.toEntity(
                History(
                    wineId = stock.wineId,
                    quantity = -1,
                    date = System.currentTimeMillis(),
                    reason = reason
                )
            )
            val historyId = historyDao.insert(history)
            stockDao.delete(stock.id)
            historyId
        }
    }

    override suspend fun withdraw(stockId: Int, reason: String): Long {
        val stock = this.getStockById(stockId) ?: return -1L
        return this.withdraw(stock, reason)
    }

    override suspend fun delete(stock: Stock) {
        val entity = StockMapper.toEntity(stock)
        stockDao.delete(entity)
    }

    override suspend fun deleteById(id: Int) {
        stockDao.delete(id)
    }
}
