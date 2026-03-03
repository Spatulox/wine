package com.spatulox.wine.ui.screens.shelf

object ShelfLayouts {
    val layouts = mapOf(
        1 to listOf(
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5)
        ),
        2 to listOf(
            listOf(1,2,3,4),
            listOf(1,2,3,4),
            listOf(1,2,3,4),
            listOf(1,2,3,4)
        ),
        3 to listOf(
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5),
            listOf(1,2,3,4,5)
        )
    )

    fun isValidPosition(shelf: Int, position: Int): Boolean {
        return layouts[shelf]?.flatten()?.contains(position) == true
    }

    fun getMaxPosition(shelf: Int): Int? = layouts[shelf]?.flatten()?.maxOrNull()
}