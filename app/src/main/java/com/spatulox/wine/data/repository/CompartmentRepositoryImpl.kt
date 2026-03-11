package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.CompartmentDao
import com.spatulox.wine.data.mapper.CompartmentMapper
import com.spatulox.wine.data.mapper.ShelfMapper
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.repository.CompartmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class CompartmentRepositoryImpl(val compartmentDao: CompartmentDao): CompartmentRepository {

    override fun getAllCompartmentsStream(): Flow<List<Compartment>> {
        return compartmentDao.getCompartmentStream().map { entities -> entities.map { CompartmentMapper.toDomain(it) } }
    }

}