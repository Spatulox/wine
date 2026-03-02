package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Stock

object StockMapper {
    fun toDomain(entity: StockEntity): Stock = Stock(
        id = entity.id,
        wineId = entity.wineId,
        shelf = entity.shelf,
        position = entity.position,
        date = entity.date
    )

    fun toEntity(stock: Stock): StockEntity = StockEntity(
        id = stock.id,
        wineId = stock.wineId,
        shelf = stock.shelf,
        position = stock.position,
        date = stock.date
    )
}
