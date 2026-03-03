package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.HistoryRepositoryImpl
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.domain.model.HistoryWithWine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

open class HistoryViewModel(
    private val historyRepository: HistoryRepositoryImpl
): FilterViewModel() {

    val history: StateFlow<List<HistoryWithWine>> =
        historyRepository.getHistoryWithWineStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}