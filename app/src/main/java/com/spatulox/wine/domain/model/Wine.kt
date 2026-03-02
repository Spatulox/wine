package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.WineFormat

data class Wine(
    val id: Int = 0,
    val name: String,
    val year: Int,
    val format: WineFormat
)
