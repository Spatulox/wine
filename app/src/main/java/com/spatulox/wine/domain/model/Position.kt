package com.spatulox.wine.domain.model

data class Position(
    val shelfId: Int,
    val row: Int,
    val col: Int
) {
    override fun toString(): String = "s:${shelfId}_r:${row}_c:${col}"
}