package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = WineEntity::class,
            parentColumns = ["id"],        // Colonne primaire de WineEntity
            childColumns = ["wineId"],     // Colonne dans HistoryEntity
            onDelete = ForeignKey.CASCADE  // Optionnel : supprime history si wine supprimé
        )
    ]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wineId: Int,

    val quantity: Int,
    val date: Long,
    val reason: String
)