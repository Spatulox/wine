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

    override suspend fun getAllCompartments(): List<Compartment> {
        return compartmentDao.getAllCompartments().map { CompartmentMapper.toDomain(it) }
    }

    override suspend fun insert(comp: Compartment, shelves: List<Shelf>): Long {

        return transactionProvider.run {
            val compID = compartmentDao.insert(CompartmentMapper.toEntity(comp))
            shelves.forEachIndexed { index, shelf ->
                shelfRepository.insert(
                    shelf.copy(compartmentId = compID.toInt(), order = index)
                )
            }
            return@run compID
        }
    }

    override suspend fun updateOrder(comps: List<Compartment>): Boolean {
        return transactionProvider.run {
            val existingComps = getAllCompartments()
            existingComps.forEach { existingComp ->
                // Avoid order update constrainst
                compartmentDao.update(CompartmentMapper.toEntity(existingComp.copy(order = comps.size + existingComp.order)))

                // Delete all comp which do not exist in the new comp list
                if (!comps.any { it.id == existingComp.id }) {
                    val stock = stockRepository.getStockByCompartmentId(existingComp.id) // If there is stock in the compartment
                    if(stock != null){
                        // Throwing (not returning) rolls the whole transaction back
                        error("Le compartiment ${existingComp.name} contient encore des bouteilles")
                    }
                    compartmentDao.delete(existingComp.id)
                }
            }

            comps.forEach { comp ->
                compartmentDao.update(CompartmentMapper.toEntity(comp))
            }

            return@run true
        }
    }

    override suspend fun update(comp: Compartment, shelves: List<Shelf>): Int {

        return transactionProvider.run {
            compartmentDao.update(CompartmentMapper.toEntity(comp))

            val existingShelves = shelfRepository.getShelvesByCompartmentId(comp.id)
            val (keptShelves, removedShelves) = existingShelves.partition { existing ->
                shelves.any { it.id == existing.id }
            }

            // Delete all shelf which do not exist in the new shelves list
            removedShelves.forEach { removed ->
                val stock = stockRepository.getStockByShelfId(removed.id) // If there is stock in the shelf
                if(stock != null){
                    // Throwing (not returning) rolls the whole transaction back
                    error("Impossible de supprimer une ligne qui contient encore des bouteilles")
                }
                shelfRepository.delete(removed.id)
            }

            // Move the kept shelves above every current and final order first, so that the
            // unique (compartmentId, order) index can't collide while renumbering
            val offset = maxOf((existingShelves.maxOfOrNull { it.order } ?: 0) + 1, shelves.size)
            keptShelves.forEach { kept ->
                shelfRepository.update(kept.copy(order = offset + kept.order))
            }

            shelves.forEachIndexed { index, shelf ->
                val ordered = shelf.copy(compartmentId = comp.id, order = index)
                if (keptShelves.any { it.id == shelf.id }) {
                    shelfRepository.update(ordered)
                } else {
                    shelfRepository.insert(ordered.copy(id = 0))
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