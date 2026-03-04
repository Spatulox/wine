package com.spatulox.wine.viewModels

import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.WineRepositoryImpl
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.Filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class WineViewModel(
    private val wineRepository: WineRepositoryImpl
) : FilterViewModel() {
    val wines: StateFlow<Map<Int, Wine>> =
        wineRepository.getWineStream()
            .map { wines -> wines.associateBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val winesByYearMap: StateFlow<Map<Int, Wine>> =
        wineRepository.getWineStream()
            .map { wines ->
                wines
                    .sortedWith(compareByDescending<Wine> { it.year }.thenBy { it.name.lowercase() })
                    .associateBy { it.id }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    val winesByYearAsc: StateFlow<List<Wine>> = wines
        .map { winesMap ->
            winesMap.values
                .sortedWith(compareBy<Wine> { it.year }.thenBy { it.name.lowercase() })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val winesByYearDesc: StateFlow<List<Wine>> = wines
        .map { winesMap ->
            winesMap.values
                .sortedWith(compareByDescending<Wine> { it.year }.thenBy { it.name.lowercase() })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val filteredWinesList: StateFlow<List<Wine>> = winesByYearDesc
        .combine(currentFilter) { winesList, filter ->
            filter?.let {
                when (it.field) {
                    "name" -> winesList
                        .filter { wine -> wine.name.equals(it.content, ignoreCase = true) }
                    "year" -> winesList
                        .filter { wine -> wine.year.toString().equals(it.content) }
                    "type" -> winesList
                        .filter { wine -> wine.type.displayName.equals(it.content, ignoreCase = true) }
                    "format" -> winesList
                        .filter { wine -> wine.format.displayName.equals(it.content, ignoreCase = true) }
                    else -> winesList
                }
            } ?: winesList
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    val filteredWinesMap: StateFlow<Map<Int, Wine>> = winesByYearMap
        .combine(currentFilter) { winesMap, filter ->
            filter?.let {
                when (it.field) {
                    "name" -> winesMap.values
                        .filter { wine -> wine.name.equals(it.content, ignoreCase = true) }
                    "year" -> winesMap.values
                        .filter { wine -> wine.year.toString().equals(it.content) }
                    "type" -> winesMap.values
                        .filter { wine -> wine.type.displayName.equals(it.content, ignoreCase = true) }
                    "format" -> winesMap.values
                        .filter { wine -> wine.format.displayName.equals(it.content, ignoreCase = true) }
                    else -> winesMap.values
                }.associateBy { it.id }
            } ?: winesMap
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )


    suspend fun addWine(wine: Wine): Boolean {
        return try {
            wineRepository.insert(wine)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun updateWine(wine: Wine){
        wineRepository.update(wine)
    }

    suspend fun deleteWine(wine: Wine): Boolean{
        return try {
            wineRepository.delete(wine)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
}
