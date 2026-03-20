package com.spatulox.wine.data.db.entity

import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity(tableName = "compartment")
data class CompartmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val order: Int
)
