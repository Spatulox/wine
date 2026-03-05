package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock

// Stock represent a single position in a shelf
object StockMapper {
    fun toDomain(entity: StockEntity): Stock = Stock(
        id = entity.id,
        wineId = entity.wineId,
        position = Position(entity.shelfId, entity.row, entity.col),
        comment = entity.comment,
        date = entity.date
    )

    fun toEntity(stock: Stock): StockEntity = StockEntity(
        id = stock.id,
        wineId = stock.wineId,
        shelfId = stock.position.shelfId,
        row = stock.position.row,
        col = stock.position.col,
        comment = stock.comment?.trim(),
        date = stock.date
    )
}
