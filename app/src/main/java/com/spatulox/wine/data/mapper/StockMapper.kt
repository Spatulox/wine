package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Stock

class StockMapper {
    fun toDomain(entity: StockEntity): Stock {
        return Stock(
            id = entity.id,
            wineId = entity.wineId,
            quantity = entity.quantity,
            placement = entity.placement,
            date = entity.date
        )
    }

    fun toEntity(stock: Stock): StockEntity {
        return StockEntity(
            id = stock.id,
            wineId = stock.wineId,
            quantity = stock.quantity,
            placement = stock.placement,
            date = stock.date
        )
    }
}