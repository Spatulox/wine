package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock

// Stock represent a single position in a shelf
object StockMapper {
    fun toDomain(entity: StockEntity): Stock = Stock(
        id = entity.id,
        wineId = entity.wineId,
        position = Position(entity.compartmentId, entity.shelfId, entity.col),
        comment = entity.comment,
        date = entity.date
    )

    fun toEntity(stock: Stock): StockEntity = StockEntity(
        id = stock.id,
        wineId = stock.wineId,
        compartmentId = stock.position.compartment,
        shelfId = stock.position.shelf,
        col = stock.position.col,
        comment = stock.comment?.trim(),
        date = stock.date
    )
}
