package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.data.db.entity.StockWithWineEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.StockWithWine

// Stock represent a single position in a shelf
object StockMapper {

    fun toDomain(entity: StockWithWineEntity): StockWithWine {
        return StockWithWine(
            id = entity.stock.id,
            wine = WineMapper.toDomain(entity.wine),
            position = Position(
                entity.stock.compartmentId,
                entity.stock.shelfId,
                entity.stock.col
            ),
            comment = entity.stock.comment,
            date = entity.stock.date
        )
    }

    fun toEntity(stock: StockWithWine): StockEntity = StockEntity(
        id = stock.id,
        wineId = stock.wine.id,
        compartmentId = stock.position.compartment,
        shelfId = stock.position.shelf,
        col = stock.position.col,
        comment = stock.comment?.trim(),
        date = stock.date
    )
}
