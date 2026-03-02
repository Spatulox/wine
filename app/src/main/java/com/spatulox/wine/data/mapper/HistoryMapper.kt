package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.domain.model.History

object HistoryMapper {
    fun toDomain(entity: HistoryEntity): History {
        return History(
            id = entity.id,
            wineId = entity.wineId,
            quantity = entity.quantity,
            date = entity.date,
            reason = entity.reason
        )
    }

    fun toEntity(history: History): HistoryEntity{
        return HistoryEntity(
            id = history.id,
            wineId = history.wineId,
            quantity = history.quantity,
            date = history.date,
            reason = history.reason
        )
    }
}