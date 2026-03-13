package com.spatulox.wine.viewModels

import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.StockRepositoryImpl
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class StockViewModel(
    private val stockRepository: StockRepositoryImpl
) : FilterViewModel() {
    val stockState: StateFlow<Map<Position, StockWithWine>> =
        stockRepository.getStockStream()
            .map { stocks ->
                stocks.associateBy { it.position }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    val stockByShelfId: StateFlow<Map<Int, List<StockWithWine>>> =
        stockRepository.getStockStream()
            .map { stocks ->
                stocks.groupBy { it.position.shelf }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )


    val countWineIdStocked: StateFlow<Map<Int, Int>> =
        stockRepository.getStockStream()
            .map { stocks ->
                stocks.groupingBy { it.wine.id }.eachCount()
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

    val stockDistinctWineCount: StateFlow<Map<Int, Int>> = stockRepository.getStockStream()
        .map { stocks ->
            stocks.groupingBy { it.wine.id }.eachCount()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    suspend fun insert(stock: StockWithWine){
        stockRepository.insert(stock, "")
    }

    suspend fun update(stock: StockWithWine){
        stockRepository.update(stock)
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
