package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.Flow


interface ShelfRepository {
    suspend fun insert(shelf: Shelf)
    suspend fun update(shelf: Shelf)
    suspend fun delete(shelfId: Int)
    suspend fun delete(shelf: Shelf)
    fun getAllShelvesStream(): Flow<List<Shelf>>
}