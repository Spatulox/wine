package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.data.db.entity.HistoryEntityWithWine
import com.spatulox.wine.domain.enum.HistoryType
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.domain.model.HistoryWithWine

object HistoryMapper {
    fun toDomain(entity: HistoryEntity): History {
        return History(
            id = entity.id,
            wineId = entity.wineId,
            type = HistoryType.valueOf(entity.type),
            date = entity.date,
            reason = entity.reason
        )
    }

    fun toEntity(history: History): HistoryEntity{
        return HistoryEntity(
            id = history.id,
            wineId = history.wineId,
            type = history.type.name,
            date = history.date,
            reason = history.reason
        )
    }

    fun toDomainWithWine(entity: HistoryEntityWithWine): HistoryWithWine {
        return HistoryWithWine(
            id = entity.history.id,
            wine = WineMapper.toDomain(entity.wine),
            type = HistoryType.valueOf(entity.history.type),
            date = entity.history.date,
            reason = entity.history.reason
        )
    }
}