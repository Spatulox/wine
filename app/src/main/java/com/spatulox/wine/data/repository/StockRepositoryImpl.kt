package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.db.dao.StockDao
import com.spatulox.wine.data.db.dao.WineDao
import com.spatulox.wine.domain.repository.StockRepository

import com.spatulox.wine.data.mapper.StockMapper
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Stock represent a single position in a shelf
class StockRepositoryImpl(
    private val stockDao: StockDao,
    private val wineDao: WineDao,
    private val transactionProvider: TransactionProvider
) : StockRepository {

    override suspend fun getStockByPos(pos: Position): StockWithWine? {
        val entity = stockDao.getStockByPos(pos.compartment, pos.shelf, pos.col)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override suspend fun hasStockInShelf(shelfId: Int): Boolean {
        return stockDao.hasStockInShelf(shelfId)
    }

    override suspend fun hasStockInCompartment(compartmentId: Int): Boolean {
        return stockDao.hasStockInCompartment(compartmentId)
    }

    override fun getStockStream(): Flow<List<StockWithWine>> {
        return stockDao.getStockStream().map { entities -> entities.map { StockMapper.toDomain(it) } }
    }

    override fun getStockYearsStream(): Flow<List<Int>> {
        return stockDao.getStockYearsStream()
    }

    override suspend fun insert(stock: StockWithWine): Long {
        val stockEntity = StockMapper.toEntity(stock)
        return stockDao.insert(stockEntity)
    }

    override suspend fun update(stock: StockWithWine){
        stockDao.update(StockMapper.toEntity(stock))
    }

    // A single UPDATE: the bottle can't be lost halfway like with a delete + insert
    override suspend fun move(stock: StockWithWine, to: Position) {
        stockDao.update(StockMapper.toEntity(stock.copy(position = to)))
    }

    // Freeing the slot and decreasing the quantity must happen together
    override suspend fun withdraw(stock: StockWithWine) {
        transactionProvider.run {
            stockDao.delete(stock.id)
            wineDao.withdrawWine(stock.wine.id)
        }
    }

    override suspend fun delete(pos: Position) {
        val entity = stockDao.getStockByPos(pos.compartment, pos.shelf, pos.col)
        if(entity != null){
            stockDao.delete(entity.stock)
        }
    }
}
