package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.ShelfDao
import com.spatulox.wine.data.mapper.ShelfMapper
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShelfRepositoryImpl(private val shelfDao: ShelfDao): ShelfRepository {
    override suspend fun insert(shelf: Shelf) {
        shelfDao.insert(ShelfMapper.toEntity(shelf))
    }

    override suspend fun update(shelf: Shelf) {
        shelfDao.update(ShelfMapper.toEntity(shelf))
    }

    override suspend fun delete(shelfId: Int) {
        shelfDao.delete(shelfId)
    }
    override suspend fun delete(shelf: Shelf) {
        shelfDao.delete(ShelfMapper.toEntity(shelf))
    }

    override fun getAllShelvesStream(): Flow<List<Shelf>> {
        return shelfDao.getShelfStream().map { entities -> entities.map { ShelfMapper.toDomain(it) } }
    }

}