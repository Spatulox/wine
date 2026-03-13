package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Stock represent a single position in a shelf
@Entity(
    tableName = "stock",
    foreignKeys = [
        ForeignKey(
            entity = WineEntity::class,
            parentColumns = ["id"],
            childColumns = ["wineId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ShelfEntity::class,
            parentColumns = ["id"],
            childColumns = ["shelfId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CompartmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["compartmentId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["compartmentId", "shelfId", "col"], unique = true),
        Index(value = ["wineId"])
    ]
)
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wineId: Int,
    val compartmentId: Int,
    val shelfId: Int,
    val col: Int,
    val comment: String?,
    val date: Long,
)