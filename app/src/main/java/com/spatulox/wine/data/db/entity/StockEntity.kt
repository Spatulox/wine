package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [
        Index(value = ["shelf", "position"], unique = true)
    ]
)
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wineId: Int,
    val shelf: Int,
    val position: Int,
    val date: Long
)