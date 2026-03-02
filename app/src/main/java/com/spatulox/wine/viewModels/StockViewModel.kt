package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.StockRepositoryImpl
import com.spatulox.wine.domain.model.Stock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class StockViewModel(
    private val stockRepository: StockRepositoryImpl
) : FilterViewModel() {
    val stockState: StateFlow<Map<Pair<Int, Int>, Stock>> =
        // Transforme List<Stock> → Map<shelf-position, Stock>
        stockRepository.getStockStream()
            .map { stocks ->
                stocks.associateBy { Pair(it.shelf, it.position) }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )
}
