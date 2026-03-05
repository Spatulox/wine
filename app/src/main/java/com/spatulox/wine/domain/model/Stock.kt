package com.spatulox.wine.domain.model

// Stock represent a single position in a shelf
data class Stock(
    val id: Int = 0,
    val wineId: Int,
    val position: Position,
    val comment: String,
    val date: Long
)