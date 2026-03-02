package com.spatulox.wine.domain.model


data class History(
    val id: Int = 0,
    val wineId: Int,
    val date: Long,
    val reason: String
)
