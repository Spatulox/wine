package com.spatulox.wine.domain.repository

import com.spatulox.wine.domain.model.Compartment
import kotlinx.coroutines.flow.Flow

interface CompartmentRepository {

    fun getAllCompartmentsStream(): Flow<List<Compartment>>

}