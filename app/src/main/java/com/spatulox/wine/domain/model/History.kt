package com.spatulox.wine.domain.model

import com.spatulox.wine.domain.enum.HistoryType


data class History(
    val id: Int = 0,
    val wineId: Int,
    val type: HistoryType,
    val date: Long,
    val reason: String?
)
