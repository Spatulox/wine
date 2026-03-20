package com.spatulox.wine.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.spatulox.wine.data.db.dao.WineDao
import com.spatulox.wine.data.mapper.StockMapper
import com.spatulox.wine.data.mapper.WineMapper
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.domain.repository.WineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId

class WineRepositoryImpl(
    private val wineDao: WineDao,
) : WineRepository {

    override suspend fun getWine(): List<Wine> {
        return wineDao.getWine().map { WineMapper.toDomain(it) }
    }

    override suspend fun getWineByPos(pos: Position): Wine? {
        val entity =  wineDao.getWineByPos(pos.compartment, pos.shelf, pos.col)
        return entity?.let { WineMapper.toDomain(it) }
    }

    override suspend fun getWineById(id: Int): Wine? {
        val entity = wineDao.getById(id)
        return entity?.let { WineMapper.toDomain(it) }
    }

    override suspend fun getWinesByYear(year: Int): List<Wine> {
        return wineDao.getByYear(year).map { WineMapper.toDomain(it) }
    }

    override fun getWineStream(): Flow<List<Wine>> {
        return wineDao.getWineStream().map { entities -> entities.map { WineMapper.toDomain(it) } }
    }

    override fun getwineYearsStream(): Flow<List<Int>> {
        return wineDao.getWineYearStream()
    }

    override suspend fun searchWines(query: String): List<Wine> {
        return wineDao.search(query).map { WineMapper.toDomain(it) }
    }

    override suspend fun insert(wine: Wine): Long {
        val entity = WineMapper.toEntity(wine)
        return wineDao.insert(entity)
    }

    override suspend fun withdrawWine(wine: Wine) {
        return wineDao.withdrawWine(wine.id)
    }

    override suspend fun delete(wineId: Int) {
        wineDao.deleteById(wineId)
    }

    override suspend fun delete(wine: Wine) {
        val entity = WineMapper.toEntity(wine)
        wineDao.delete(entity)
    }

    override suspend fun update(wine: Wine): Int {
        val entity = WineMapper.toEntity(wine)
        return wineDao.update(entity)
    }
}