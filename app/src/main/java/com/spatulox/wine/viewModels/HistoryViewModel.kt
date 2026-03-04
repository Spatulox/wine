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


    val filteredhistoryList: StateFlow<List<HistoryWithWine>> = history
        .combine(currentFilter) { historyList, filter ->
            filter?.let {
                when (it.field) {
                    "name" -> historyList
                        .filter { history -> history.wine.name.contains(it.content, ignoreCase = true) }
                    "year" -> historyList
                        .filter { history ->
                            val historyYear = LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(history.date), ZoneId.systemDefault())
                                .year
                                .toString()
                            historyYear.contains(it.content)
                        }
                    "type" -> historyList
                        .filter { history -> history.wine.type.displayName.contains(it.content, ignoreCase = true) }
                    "format" -> historyList
                        .filter { history -> history.wine.format.displayName.contains(it.content, ignoreCase = true) }
                    else -> historyList
                }
            } ?: historyList
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val historyYears: StateFlow<List<Int>> =
        historyRepository.getHistoryYearStream()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

}