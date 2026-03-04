package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.StockRepositoryImpl
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

open class StockViewModel(
    private val stockRepository: StockRepositoryImpl
) : FilterViewModel() {
    val stockState: StateFlow<Map<Position, Stock>> =
        stockRepository.getStockStream()
            .map { stocks ->
                stocks.associateBy { it.position }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    val stockYears: StateFlow<List<Int>> =
        stockRepository.getStockYearsStream()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    suspend fun insert(position: Position, wine: Wine, reason: String){
        val stock = Stock(
            wineId = wine.id,
            position = position,
            date = System.currentTimeMillis()
        )
        stockRepository.insert(stock, reason)
    }

    suspend fun withdraw(position: Position, reason: String){
        val entity = stockRepository.getStockByPos(position)
        if(entity != null){
            stockRepository.withdraw(entity, reason)
        }
    }

    suspend fun delete(position: Position){
        stockRepository.delete(position)
    }
}
