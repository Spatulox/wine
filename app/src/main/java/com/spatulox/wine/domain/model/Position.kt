package com.spatulox.wine.domain.model

data class Position(
    val compartment: Int,
    val shelf: Int,
    val col: Int
) {
    override fun toString(): String = "comp:${compartment}_shelf:${shelf}_col:${col}"
}