package com.spatulox.wine.viewModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.repository.CompartmentRepository
import com.spatulox.wine.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CompartmentViewModel(
    private val compartmentRepository: CompartmentRepository,
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

    // Reads the database: unlike compartments.value, works before the stream has emitted
    suspend fun loadCompartment(id: Int): Compartment? {
        return compartmentRepository.getById(id)
    }

    suspend fun loadShelves(compartmentId: Int): List<Shelf> {
        return shelfRepository.getShelvesByCompartmentId(compartmentId).sortedBy { it.order }
    }

    // Returns null on success, or an error message
    suspend fun insert(compartment: Compartment, shelves: List<Shelf>): String? {
        return try {
            compartmentRepository.insert(compartment, shelves)
            null
        } catch (e: SQLiteConstraintException) {
            "Impossible de créer le compartiment"
        }
    }

    suspend fun updateOrder(compartments: List<Compartment>): Boolean {
        return try {
            compartmentRepository.updateOrder(compartments)
        } catch (e: SQLiteConstraintException) {
            false
        } catch (e: IllegalStateException) {
            false
        }
    }

    // Returns null on success, or an error message
    suspend fun update(compartment: Compartment, shelves: List<Shelf>): String? {
        return try {
            compartmentRepository.update(compartment, shelves)
            null
        } catch (e: SQLiteConstraintException) {
            "Impossible de mettre à jour le compartiment"
        } catch (e: IllegalStateException) {
            e.message ?: "Impossible de mettre à jour le compartiment"
        }
    }

    suspend fun delete(compartment: Compartment): String? {
        return try {
            compartmentRepository.delete(compartment)
        } catch (e: SQLiteConstraintException) {
            "Impossible de supprimer le compartiment"
        }
    }
}