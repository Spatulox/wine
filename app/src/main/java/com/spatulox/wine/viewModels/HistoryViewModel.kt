package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.HistoryRepositoryImpl
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.domain.model.HistoryWithWine
import com.spatulox.wine.domain.model.Wine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

open class HistoryViewModel(
    private val historyRepository: HistoryRepositoryImpl
): FilterViewModel() {

    val history: StateFlow<List<HistoryWithWine>> =
        historyRepository.getHistoryWithWineStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val filteredHistoryList: StateFlow<List<HistoryWithWine>> = history
        .combine(currentFilter) { list, filter ->
            applyFilter(
                items = list,
                filter = filter,
                getName = { it.wine.name },
                getYear = {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it.date), ZoneId.systemDefault()).year.toString()
                },
                getType = { it.wine.type.displayName },
                getFormat = { it.wine.format.displayName }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyYears: StateFlow<List<Int>> =
        historyRepository.getHistoryYearStream()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

}