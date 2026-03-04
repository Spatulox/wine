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
            rows = entity.rows,
            cols = entity.cols,
            interleave = ShelfInterleave.valueOf(entity.interleave),
            bottlePositionning = BottlePosition.valueOf(entity.bottlePositionning)
        )
    }

    fun toEntity(shelf: Shelf): ShelfEntity{
        return ShelfEntity(
            id = shelf.id,
            name = shelf.name,
            rows = shelf.rows,
            cols = shelf.cols,
            interleave = shelf.interleave.name,
            bottlePositionning = shelf.bottlePositionning.name
        )
    }
}