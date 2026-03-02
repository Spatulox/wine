package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "wine")
data class WineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val year: String,
    val date: Long
)