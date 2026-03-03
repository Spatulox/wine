package com.spatulox.wine.domain.model

data class Position(
    val shelf: Int,
    val row: Int,
    val col: Int
) {
    override fun toString(): String = "s:${shelf}_r:${row}_c:${col}"
}