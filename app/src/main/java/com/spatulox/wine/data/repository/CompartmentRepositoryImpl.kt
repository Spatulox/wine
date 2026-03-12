package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.db.dao.CompartmentDao
import com.spatulox.wine.data.mapper.CompartmentMapper
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.repository.CompartmentRepository
import com.spatulox.wine.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class CompartmentRepositoryImpl(val compartmentDao: CompartmentDao, val shelfRepository: ShelfRepository, val transactionProvider: TransactionProvider): CompartmentRepository {

    override fun getAllCompartmentsStream(): Flow<List<Compartment>> {
        return compartmentDao.getCompartmentStream().map { entities -> entities.map { CompartmentMapper.toDomain(it) } }
    }

    override suspend fun insert(comp: Compartment, shelves: List<Shelf>): Long {

        val compID = compartmentDao.insert(CompartmentMapper.toEntity(comp))
        transactionProvider.run {
            shelves.forEach { shelf ->
                shelfRepository.insert(
                    shelf.copy(compartmentId = compID.toInt())
                )
            }
        }
        return compID
    }

}