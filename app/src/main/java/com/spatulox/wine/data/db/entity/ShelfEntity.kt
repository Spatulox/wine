package com.spatulox.wine.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shelf",
    foreignKeys = [
        ForeignKey(
            entity = CompartmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["compartmentId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["compartmentId", "order"], unique = true)
    ]
)
data class ShelfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val compartmentId: Int,
    val order: Int,
    val col: Int,
    val aligment: String,
    val arrangement: String
)