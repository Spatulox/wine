package com.spatulox.wine.viewModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.CompartmentRepositoryImpl
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CompartmentViewModel(
    private val compartmentRepository: CompartmentRepositoryImpl,
    private val shelfRepository: ShelfRepository
) : ViewModel() {

    private val _isEditingOrder = MutableStateFlow(false)
    val isEditingOrder: StateFlow<Boolean> = _isEditingOrder.asStateFlow()

    fun setEditingOrder(editing: Boolean) {
        _isEditingOrder.value = editing
    }

    val compartments: StateFlow<List<Compartment>> = compartmentRepository
        .getAllCompartmentsStream()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    fun getCompartmentById(id: Int): Compartment? {
        return compartments.value.find { it.id == id }
    }

    suspend fun insert(compartment: Compartment, shelves: List<Shelf>) {
        compartmentRepository.insert(compartment, shelves)
    }

    suspend fun updateOrder(compartments: List<Compartment>): Boolean {
        return try {
            compartmentRepository.updateOrder(compartments)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun update(compartment: Compartment, shelves: List<Shelf>): Boolean {
        return try {
            compartmentRepository.update(compartment, shelves)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun delete(compartment: Compartment): String? {
        return try {
            return compartmentRepository.delete(compartment)
        } catch (e: SQLiteConstraintException) {
            e.toString()
        }
    }
}