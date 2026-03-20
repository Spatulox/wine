package com.spatulox.wine.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StockWithWineEntity(
    @Embedded
    val stock: StockEntity,

    @Relation(
        parentColumn = "wineId",
        entityColumn = "id"
    )
    val wine: WineEntity
)