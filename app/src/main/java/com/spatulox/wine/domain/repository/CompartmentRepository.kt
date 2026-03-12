package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.Flow

interface CompartmentRepository {
    suspend fun insert(comp: Compartment, shelves: List<Shelf>): Long
    suspend fun update(comp: Compartment, shelves: List<Shelf>): Int
    suspend fun delete(comp: Compartment): String?
    fun getAllCompartmentsStream(): Flow<List<Compartment>>

}