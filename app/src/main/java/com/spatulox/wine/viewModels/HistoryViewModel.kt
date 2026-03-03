package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.HistoryRepositoryImpl
import com.spatulox.wine.domain.model.History
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

open class HistoryViewModel(
    private val historyRepository: HistoryRepositoryImpl
): FilterViewModel() {

    val history: StateFlow<List<History>> =
        historyRepository.getHistoryStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}