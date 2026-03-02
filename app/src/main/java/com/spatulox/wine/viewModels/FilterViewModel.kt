package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import com.spatulox.wine.ui.screens.components.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class FilterViewModel: ViewModel() {
    private val _currentFilter = MutableStateFlow<Filter?>(null)
    val currentFilter: StateFlow<Filter?> = _currentFilter.asStateFlow()

    fun updateFilter(filter: Filter) {
        _currentFilter.value = if (filter.content.isBlank()) null else filter
    }

    fun clearFilter() {
        _currentFilter.value = null
    }
}