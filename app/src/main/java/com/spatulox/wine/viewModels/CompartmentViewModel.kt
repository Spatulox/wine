package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.CompartmentRepositoryImpl
import com.spatulox.wine.domain.model.Compartment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CompartmentViewModel(private val compartmentRepository: CompartmentRepositoryImpl): ViewModel() {
    val compartments: StateFlow<List<Compartment>> = compartmentRepository
        .getAllCompartmentsStream()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}