package com.spatulox.wine.domain.repository

import androidx.room.Query
import com.spatulox.wine.data.db.entity.CompartmentEntity
import com.spatulox.wine.data.db.entity.ShelfEntity
import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.Flow


interface ShelfRepository {

    suspend fun get(id: Int): Shelf?
    suspend fun insert(shelf: Shelf)
    suspend fun update(shelf: Shelf)
    suspend fun delete(shelfId: Int)
    suspend fun delete(shelf: Shelf)
    fun getAllShelvesStream(): Flow<List<Shelf>>
    suspend fun getShelvesByCompartmentId(compartmentId: Int): List<Shelf>
}