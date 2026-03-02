package com.spatulox.wine.domain.model

data class Stock(
    val id: Int = 0,
    val wineId: Int,
    val shelf: Int,           // "1"
    val position: Int,        // "2"
    val location: String = "$shelf:$position",
    val date: Long
)