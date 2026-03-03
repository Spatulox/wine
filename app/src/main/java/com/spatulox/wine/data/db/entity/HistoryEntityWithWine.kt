package com.spatulox.wine.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryEntityWithWine(
    @Embedded val history: HistoryEntity,
    @Relation(
        parentColumn = "wineId",
        entityColumn = "id"
    )
    val wine: WineEntity
)