package com.spatulox.wine.viewModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spatulox.wine.data.repository.ShelfRepositoryImpl
import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ShelfViewModel(private val shelfRepository: ShelfRepositoryImpl): ViewModel() {

    val shelves: StateFlow<List<Shelf>> = shelfRepository
        .getAllShelvesStream()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    suspend fun insert(shelf: Shelf){
        shelfRepository.insert(shelf)
    }

    suspend fun update(shelf: Shelf){
        shelfRepository.update(shelf)
    }

    suspend fun delete(shelfId: Int): Boolean{
        return try {
            shelfRepository.delete(shelfId)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
    suspend fun delete(shelf: Shelf): Boolean{
        return this.delete(shelf.id)
    }

}