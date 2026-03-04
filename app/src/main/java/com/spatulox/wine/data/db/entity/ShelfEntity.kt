package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelf")
data class ShelfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val rows: Int,
    val cols: Int,
    val interleave: String,
    val bottlePositionning: String
)