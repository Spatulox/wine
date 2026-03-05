package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType

data class Wine(
    val id: Int = 0,
    val name: String,
    val year: Int,
    val format: WineFormat,
    val type: WineType,
    val unitPrice: Float?,
    var stars: Int,
    val qte: Int,
    val region: WineRegion?
)
