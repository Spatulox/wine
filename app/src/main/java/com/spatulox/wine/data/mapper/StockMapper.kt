package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock

object StockMapper {
    fun toDomain(entity: StockEntity): Stock = Stock(
        id = entity.id,
        wineId = entity.wineId,
        position = Position(entity.shelf, entity.row, entity.col),
        date = entity.date
    )

    fun toEntity(stock: Stock): StockEntity = StockEntity(
        id = stock.id,
        wineId = stock.wineId,
        shelf = stock.position.shelf,
        row = stock.position.row,
        col = stock.position.col,
        date = stock.date
    )
}
