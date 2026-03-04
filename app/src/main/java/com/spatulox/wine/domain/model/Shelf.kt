package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave

data class Shelf(
    val id: Int = 0,
    val name: String,
    val rows: Int,
    val cols: Int,
    val interleave: ShelfInterleave,
    val bottlePositionning: BottlePosition
) {
}