package com.spatulox.wine.viewModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.domain.repository.StockRepository
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class StockViewModel(
    private val stockRepository: StockRepository
) : FilterViewModel() {
    // Single Room query (with its @Relation) shared by every derived state below
    private val stocks: StateFlow<List<StockWithWine>> =
        stockRepository.getStockStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockState: StateFlow<Map<Position, StockWithWine>> = stocks
        .map { stocks -> stocks.associateBy { it.position } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val stockByShelfId: StateFlow<Map<Int, List<StockWithWine>>> = stocks
        .map { stocks -> stocks.groupBy { it.position.shelf } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Number of racked bottles per wine id
    val countWineIdStocked: StateFlow<Map<Int, Int>> = stocks
        .map { stocks -> stocks.groupingBy { it.wine.id }.eachCount() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val stockYears: StateFlow<List<Int>> =
        stockRepository.getStockYearsStream()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    suspend fun insert(stock: StockWithWine){
        stockRepository.insert(stock, "")
    }

    suspend fun update(stock: StockWithWine){
        stockRepository.update(stock)
    }

    // Returns false when the target position is already taken
    suspend fun move(stock: StockWithWine, to: Position): Boolean {
        return try {
            stockRepository.move(stock, to)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
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
