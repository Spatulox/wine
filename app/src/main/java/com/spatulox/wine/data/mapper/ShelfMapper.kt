package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.ShelfEntity
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Shelf

object ShelfMapper {
    fun toDomain(entity: ShelfEntity): Shelf{
        return Shelf(
            id = entity.id,
            name = entity.name,
            compartmentId = entity.compartmentId,
            order = entity.order,
            col = entity.col,
            aligment = ShelfInterleave.valueOf(entity.aligment),
            arrangement = BottlePosition.valueOf(entity.arrangement)
        )
    }

    fun toEntity(shelf: Shelf): ShelfEntity{
        return ShelfEntity(
            id = shelf.id,
            name = shelf.name.trim(),
            compartmentId = shelf.compartmentId,
            order = shelf.order,
            col = shelf.col,
            aligment = shelf.aligment.name,
            arrangement = shelf.arrangement.name
        )
    }
}