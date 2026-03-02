package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock",
    foreignKeys = [
        ForeignKey(
            entity = WineEntity::class,
            parentColumns = ["id"],
            childColumns = ["wineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wineId: Int,

    val quantity: Int,
    val placement: String,
    val date: Long
)