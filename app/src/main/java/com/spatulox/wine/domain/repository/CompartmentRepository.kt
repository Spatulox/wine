package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.Flow

interface CompartmentRepository {
    suspend fun insert(comp: Compartment, shelves: List<Shelf>): Long
    fun getAllCompartmentsStream(): Flow<List<Compartment>>

}