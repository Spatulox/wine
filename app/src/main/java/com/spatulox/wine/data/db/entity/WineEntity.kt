package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "wine",
    indices = [
        Index(value = ["name", "year", "format"], unique = true)
    ]
)
data class WineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val year: Int,
    val format: String,
    val type: String,
    var stars: Int
)