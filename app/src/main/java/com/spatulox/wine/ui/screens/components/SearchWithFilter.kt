package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

data class Filter(
    val content: String,
    val field: String  // "name", "year", "format"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWithFilters(
    modifier: Modifier = Modifier,
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    historyViewModel: HistoryViewModel
) {
    //val wineViewModel: WineViewModel = LocalViewModelProvider.current[WineViewModel::class]
    //val stockViewModel: StockViewModel = LocalViewModelProvider.current[StockViewModel::class]
    //val historyViewModel: HistoryViewModel = LocalViewModelProvider.current[HistoryViewModel::class]

    var searchText by remember { mutableStateOf("") }
    var selectedField by remember { mutableStateOf("name") }
    var expanded by remember { mutableStateOf(false) }

    val filterFields = listOf(
        "name" to "Nom",
        "year" to "Année",
        "format" to "Format"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                    val filter = Filter(content = text, field = selectedField)
                    wineViewModel.updateFilter(filter)
                    stockViewModel.updateFilter(filter)
                    historyViewModel.updateFilter(filter)
                },
                label = { Text("Rechercher...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = filterFields.find { it.first == selectedField }?.second ?: selectedField,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrer par") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filterFields.forEach { (field, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedField = field
                                expanded = false
                                val filter = Filter(content = searchText, field = field)
                                wineViewModel.updateFilter(filter)
                                stockViewModel.updateFilter(filter)
                                historyViewModel.updateFilter(filter)
                            }
                        )
                    }
                }
            }
        }
    }
}
