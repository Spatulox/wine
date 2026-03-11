package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave

data class Shelf(
    val id: Int = 0,
    val name: String,
    val compartmentId: Int,
    val col: Int,
    val aligment: ShelfInterleave,
    val arrangement: BottlePosition
) {
}