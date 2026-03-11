package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class FilterViewModel: ViewModel() {
    private val _currentFilter = MutableStateFlow<Filter?>(null)
    val currentFilter: StateFlow<Filter?> = _currentFilter.asStateFlow()


    fun <T> applyFilter(
        items: List<T>,
        filter: Filter?,
        getId: (T) -> String = { (it as? Wine)?.id.toString() },
        getName: (T) -> String = { (it as? Wine)?.name ?: "" },
        getYear: (T) -> String = { (it as? Wine)?.year?.toString() ?: "" },
        getRegion: (T) -> String = { (it as? Wine)?.region?.displayName ?: "" },
        getType: (T) -> String = { (it as? Wine)?.type?.displayName ?: "" },
        getFormat: (T) -> String = { (it as? Wine)?.format?.displayName ?: "" }
    ): List<T> = filter?.let { f ->
        when (f.field) {
            "wineId" -> items.filter { getId(it).equals(f.content, ignoreCase = true) }
            "name" -> items.filter { getName(it).equals(f.content, ignoreCase = true) }
            "year" -> items.filter { getYear(it) == f.content } // Int
            "region" -> items.filter { getRegion(it).equals(f.content, ignoreCase = true) }
            "type" -> items.filter { getType(it).equals(f.content, ignoreCase = true) }
            "format" -> items.filter { getFormat(it).equals(f.content, ignoreCase = true) }
            else -> items
        }
    } ?: items

    fun updateFilter(filter: Filter) {
        _currentFilter.value = if (filter.content.isBlank()) null else filter
    }

    fun clearFilter() {
        _currentFilter.value = null
    }
}