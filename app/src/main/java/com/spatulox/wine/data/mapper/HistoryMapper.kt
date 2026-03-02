package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.domain.model.History

class HistoryMapper {
    fun toDomain(entity: HistoryEntity): History {
        return History(
            id = entity.id,
            date = entity.date
        )
    }

    fun toEntity(history: History): HistoryEntity{
        return HistoryEntity(
            id = history.id,
            date = history.date
        )
    }
}