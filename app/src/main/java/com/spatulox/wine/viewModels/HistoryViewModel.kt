package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import com.spatulox.wine.data.repository.HistoryRepositoryImpl

open class HistoryViewModel(
    private val historyRepository: HistoryRepositoryImpl
): FilterViewModel() {
}