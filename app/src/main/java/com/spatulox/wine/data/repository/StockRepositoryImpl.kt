package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.db.dao.StockDao
import com.spatulox.wine.domain.repository.StockRepository

import com.spatulox.wine.data.mapper.StockMapper
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Stock represent a single position in a shelf
class StockRepositoryImpl(
    private val stockDao: StockDao,
    private val transactionProvider: TransactionProvider
) : StockRepository {

    override suspend fun getStock(): List<StockWithWine> {
        return stockDao.getStock().map { StockMapper.toDomain(it) }
    }

    override suspend fun getStockById(id: Int): StockWithWine? {
        val entity = stockDao.getStockById(id)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override suspend fun getStockByPos(pos: Position): StockWithWine? {
        val entity = stockDao.getStockByPos(pos.compartment, pos.shelf, pos.col)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override suspend fun getStockByShelfId(id: Int): StockWithWine? {
        val entity =  stockDao.getStockByShelfId(id)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override suspend fun getStockByCompartmentId(id: Int): StockWithWine? {
        val entity =  stockDao.getStockByCompartmentId(id)
        return entity?.let { StockMapper.toDomain(it) }
    }

    override fun getStockStream(): Flow<List<StockWithWine>> {
        return stockDao.getStockStream().map { entities -> entities.map { StockMapper.toDomain(it) } }
    }

    override fun getStockYearsStream(): Flow<List<Int>> {
        return stockDao.getStockYearsStream()
    }

    override suspend fun insert(stock: StockWithWine, reason: String): Long {
        val stockEntity = StockMapper.toEntity(stock)
        return stockDao.insert(stockEntity)
    }

    override suspend fun update(stock: StockWithWine){
        stockDao.update(StockMapper.toEntity(stock))
    }

    override suspend fun withdraw(stock: StockWithWine, reason: String){
        stockDao.delete(stock.id)
    }

    override suspend fun withdraw(stockId: Int, reason: String) {
        val stock = this.getStockById(stockId) ?: return
        this.withdraw(stock , reason)
    }

    override suspend fun delete(stock: StockWithWine) {
        val entity = StockMapper.toEntity(stock)
        stockDao.delete(entity)
    }

    override suspend fun delete(pos: Position) {
        val entity = stockDao.getStockByPos(pos.compartment, pos.shelf, pos.col)
        if(entity != null){
            stockDao.delete(entity.stock)
        }
    }

    override suspend fun delete(stockId: Int) {
        stockDao.delete(stockId)
    }
}
