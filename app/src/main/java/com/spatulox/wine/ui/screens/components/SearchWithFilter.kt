package com.spatulox.wine.ui.screens.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.wine.WineDropdownList
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

data class Filter(
    val content: String,
    val field: String
)

data class FilterOption(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val enumClass: KClass<out Enum<*>>? = null
)

val filterFields = listOf(
    FilterOption("name", "Nom", Icons.Filled.Person),
    FilterOption("year", "Année", Icons.Filled.DateRange),
    FilterOption("region", "Region", Icons.Filled.LocationOn, WineRegion::class),
    FilterOption("format", "Format", Icons.Filled.Liquor, WineFormat::class),
    FilterOption("type", "Type", Icons.Filled.WineBar, WineType::class),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWithFilters(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    historyViewModel: HistoryViewModel,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
) {
    var isFilterPopupVisible by remember { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("name") }

    var selectedWine by remember { mutableStateOf<Wine?>(null) }
    var year by remember(selectedTabIndex) { mutableStateOf<Int?>(null) }
    var selectedWineType by remember { mutableStateOf<WineType?>(null) }
    var selectedWineFormat by remember { mutableStateOf<WineFormat?>(null) }
    var selectedWineRegion by remember { mutableStateOf<WineRegion?>(null) }

    var isNameInit by remember { mutableStateOf(true) }
    var isDateInit by remember { mutableStateOf(true) }
    var isFormatInit by remember { mutableStateOf(true) }
    var isTypeInit by remember { mutableStateOf(true) }

    val stockYears by stockViewModel.stockYears.collectAsStateWithLifecycle()
    val wineYears by wineViewModel.winesYears.collectAsStateWithLifecycle()
    val historyYears by historyViewModel.historyYears.collectAsStateWithLifecycle()
    val availableYears = when(selectedTabIndex) {
        0 -> stockYears
        1 -> wineYears
        2 -> historyYears
        else -> emptyList()
    }

    val wineState by wineViewModel.wines.collectAsStateWithLifecycle()
    val countStockedWine by stockViewModel.countWineIdStocked.collectAsStateWithLifecycle()
    val excludeWineIds = when(selectedTabIndex) {
        0 -> {
            (countStockedWine.entries
                .filter { it.value == 0 }.map { it.key } +
                    wineState.keys.filter { !countStockedWine.containsKey(it) }
                    ).toList()
        }
        else -> emptyList()
    }



    System.out.println(countStockedWine)
    System.out.println(excludeWineIds)

    FloatingActionButton(
        onClick = {
            onExpandedChange(!isExpanded)
            if (!isExpanded) {
                isNameInit = true
                isDateInit = true
                isFormatInit = true
                isTypeInit = true
                isFilterPopupVisible = false
                wineViewModel.clearFilter()
                stockViewModel.clearFilter()
                historyViewModel.clearFilter()
            }
        },
        modifier = modifier
            .padding(24.dp)
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.large
    ) {
        AnimatedContent(targetState = isExpanded, label = "fab_animation") { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    when(selectedField) {

                        "name" -> {
                            if(isExpanded && isNameInit){
                                isNameInit = false
                                selectedWine?.let { wine ->
                                    val filter = Filter(content = wine.name, field = "name")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                }
                            }
                            WineDropdownList(
                                wineViewModel = wineViewModel,
                                selectedWine = selectedWine,
                                excludeWineId = excludeWineIds,
                                distinctWineList = true,
                                onSelectWine = { wine ->
                                    selectedWine = wine
                                    val filter = Filter(content = wine.name, field = "name")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        "year" -> {
                            if(isExpanded && isDateInit){
                                isDateInit = false
                                year?.let { year ->
                                    val filter = Filter(content = year.toString(), field = "year")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                }
                            }
                            DateSelection(
                                year = year,
                                availableYears = availableYears,
                                onYearChange = { lyear ->
                                    year = lyear
                                    val filter = Filter(content = lyear.toString(), field = "year")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        "type" -> {
                            if(isExpanded && isTypeInit){
                                isTypeInit = false
                                selectedWineType?.let { type ->
                                    val filter = Filter(content = type.name, field = "type")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                }
                            }
                            EnumDropdownField(
                                selectedEnum = selectedWineType,
                                enumClass = WineType::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineType = enumValue as WineType
                                    val filter = Filter(content = displayName, field = "type")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner type...",
                                invisibleBorder = true
                            )
                        }

                        "format" -> {
                            if(isExpanded && isFormatInit){
                                isFormatInit = false
                                selectedWineFormat?.let { format ->
                                    val filter = Filter(content = format.name, field = "format")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                }
                            }
                            EnumDropdownField(
                                selectedEnum = selectedWineFormat,
                                enumClass = WineFormat::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineFormat = enumValue as WineFormat
                                    val filter = Filter(content = displayName, field = "format")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner format...",
                                invisibleBorder = true
                            )
                        }

                        "region" -> {
                            if(isExpanded && isFormatInit){
                                isFormatInit = false
                                selectedWineRegion?.let { format ->
                                    val filter = Filter(content = format.name, field = "region")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                }
                            }
                            EnumDropdownField(
                                selectedEnum = selectedWineRegion,
                                enumClass = WineRegion::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineRegion = enumValue as WineRegion
                                    val filter = Filter(content = displayName, field = "region")
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner region...",
                                invisibleBorder = true
                            )
                        }
                    }

                    IconButton(
                        onClick = { isFilterPopupVisible = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Tune,
                            contentDescription = "Filtres",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }


    if (isFilterPopupVisible && isExpanded) {
        Dialog(
            onDismissRequest = { isFilterPopupVisible = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Filtres",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Un seul filtre actif à la fois",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(filterFields.size) { index ->
                            val (field, icon) = filterFields[index]
                            FilterButton(
                                selected = selectedField == field,
                                option = filterFields[index],
                                onClick = {
                                    selectedField = field
                                    val filter = Filter(content = "", field = field)
                                    wineViewModel.updateFilter(filter)
                                    stockViewModel.updateFilter(filter)
                                    historyViewModel.updateFilter(filter)
                                    isFilterPopupVisible = false
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { isFilterPopupVisible = false }
                        ) {
                            Text("Fermer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterButton(
    selected: Boolean,
    option: FilterOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = option.name,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}