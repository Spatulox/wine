package com.spatulox.wine.domain.model

data class StockWithWine(
    val id: Int = 0,
    val wine: Wine,
    val position: Position,
    val comment: String?,
    val date: Long
)