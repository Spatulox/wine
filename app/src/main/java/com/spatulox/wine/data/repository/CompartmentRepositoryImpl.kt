package com.spatulox.wine.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.db.dao.CompartmentDao
import com.spatulox.wine.data.mapper.CompartmentMapper
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.repository.CompartmentRepository
import com.spatulox.wine.domain.repository.ShelfRepository
import com.spatulox.wine.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class CompartmentRepositoryImpl(val compartmentDao: CompartmentDao, val shelfRepository: ShelfRepository, val stockRepository: StockRepository, val transactionProvider: TransactionProvider): CompartmentRepository {

    override fun getAllCompartmentsStream(): Flow<List<Compartment>> {
        return compartmentDao.getCompartmentStream().map { entities -> entities.map { CompartmentMapper.toDomain(it) } }
    }

    override suspend fun insert(comp: Compartment, shelves: List<Shelf>): Long {

        return transactionProvider.run {
            val compID = compartmentDao.insert(CompartmentMapper.toEntity(comp))
            shelves.forEach { shelf ->
                shelfRepository.insert(
                    shelf.copy(compartmentId = compID.toInt())
                )
            }
            return@run compID
        }
    }

    override suspend fun update(comp: Compartment, shelves: List<Shelf>): Int {

        return transactionProvider.run {
            compartmentDao.update(CompartmentMapper.toEntity(comp))

            val existingShelves = shelfRepository.getShelvesByCompartmentId(comp.id)
            existingShelves.forEach { existingShelf ->
                // Avoid order update constrainst
                shelfRepository.update(existingShelf.copy(order = 1000 + existingShelf.order))

                // Delete all shelf
                if (!shelves.any { it.id == existingShelf.id }) {
                    val stock = stockRepository.getStockByShelfId(existingShelf.id)
                    if(stock != null){
                        return@run -1
                    }
                    shelfRepository.delete(existingShelf.id)
                }
            }

            shelves.forEach { shelf ->
                val sh = shelfRepository.get(shelf.id)
                if(sh != null){
                    shelfRepository.update(shelf)
                } else {
                    shelfRepository.insert(shelf)
                }
            }
            return@run comp.id
        }
    }

    override suspend fun delete(comp: Compartment): String? {

        return transactionProvider.run {
            val stock = stockRepository.getStockByCompartmentId(comp.id)
            if(stock != null){
                return@run "There is stock inside the compartment you want to delete"
            }
            val shelf = shelfRepository.getShelvesByCompartmentId(comp.id)

            try {
                shelf.forEach { shelf ->
                    shelfRepository.delete(shelf)
                }
            } catch (e: SQLiteConstraintException) {
                return@run "Can't delete all the shelf from the compartment"
            }

            compartmentDao.delete(CompartmentMapper.toEntity(comp))
            return@run null
        }
    }

}