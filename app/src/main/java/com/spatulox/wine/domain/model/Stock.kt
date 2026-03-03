package com.spatulox.wine.domain.model

data class Stock(
    val id: Int = 0,
    val wineId: Int,
    val position: Position,
    val date: Long
)