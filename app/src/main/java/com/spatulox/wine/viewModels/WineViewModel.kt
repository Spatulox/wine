package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.WineRepositoryImpl
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.Filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class WineViewModel(
    private val wineRepository: WineRepositoryImpl
) : FilterViewModel() {
    val wines: StateFlow<Map<Int, Wine>> =
        wineRepository.getWineStream()
            .map { wines -> wines.associateBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    suspend fun addWine(wine: Wine){
        wineRepository.insert(wine)
    }
    suspend fun updateWine(wine: Wine){
        wineRepository.update(wine)
    }

    suspend fun deleteWine(wine: Wine){
        wineRepository.delete(wine)
    }
}
