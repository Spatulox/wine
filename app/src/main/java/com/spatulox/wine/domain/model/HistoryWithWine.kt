package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.HistoryType


data class HistoryWithWine(
    val id: Int = 0,
    val wine: Wine,
    val type: HistoryType,
    val date: Long,
    val reason: String?
)
