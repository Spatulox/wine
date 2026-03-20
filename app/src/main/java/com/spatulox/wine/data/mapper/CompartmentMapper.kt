package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.CompartmentEntity
import com.spatulox.wine.domain.model.Compartment

object CompartmentMapper {
    fun toDomain(entity: CompartmentEntity): Compartment {
        return Compartment(
            id = entity.id,
            name = entity.name,
            order = entity.order
        )
    }

    fun toEntity(compartment: Compartment): CompartmentEntity {
        return CompartmentEntity(
            id = compartment.id,
            name = compartment.name.trim(),
            order = compartment.order
        )
    }
}