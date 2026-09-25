package com.spatulox.wine.ui.screens.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.wine.WineDropdownList
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
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
    FilterOption("wineId", "Nom distinct", Icons.Filled.PersonSearch),
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

    fun applyFilter(filter: Filter?) {
        if (filter == null) {
            wineViewModel.clearFilter()
            stockViewModel.clearFilter()
        } else {
            wineViewModel.updateFilter(filter)
            stockViewModel.updateFilter(filter)
        }
    }

    // The filter matching what the search bar shows for a field. Enums always use their
    // displayName, which is what FilterViewModel.applyFilter compares against
    fun selectionFilter(field: String): Filter? = when (field) {
        "name" -> selectedWine?.let { Filter(content = it.name, field = "name") }
        "wineId" -> selectedWine?.let { Filter(content = it.id.toString(), field = "wineId") }
        "year" -> year?.let { Filter(content = it.toString(), field = "year") }
        "type" -> selectedWineType?.let { Filter(content = it.displayName, field = "type") }
        "format" -> selectedWineFormat?.let { Filter(content = it.displayName, field = "format") }
        "region" -> selectedWineRegion?.let { Filter(content = it.displayName, field = "region") }
        else -> null
    }

    val stockYears by stockViewModel.stockYears.collectAsStateWithLifecycle()
    val wineYears by wineViewModel.winesYears.collectAsStateWithLifecycle()
    val availableYears = when(selectedTabIndex) {
        0 -> stockYears
        1 -> wineYears
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

    // A filter set from elsewhere (a wine clicked in the wine list) is shown in the search bar
    val activeFilter by wineViewModel.currentFilter.collectAsStateWithLifecycle()
    LaunchedEffect(activeFilter) {
        val filter = activeFilter ?: return@LaunchedEffect
        if (filter.field == "wineId" && filter != selectionFilter(selectedField)) {
            selectedField = "wineId"
            selectedWine = wineState[filter.content.toIntOrNull()]
        }
    }

    FloatingActionButton(
        onClick = {
            onExpandedChange(!isExpanded)
            isFilterPopupVisible = false
            // Reopening the search restores the previous selection, closing it removes the filter
            applyFilter(if (!isExpanded) selectionFilter(selectedField) else null)
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
                            WineDropdownList(
                                wineViewModel = wineViewModel,
                                selectedWine = selectedWine,
                                excludeWineId = excludeWineIds,
                                distinctWineList = true,
                                onSelectWine = { wine ->
                                    selectedWine = wine
                                    applyFilter(selectionFilter("name"))
                                },
                                modifier = Modifier.weight(1f),
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        "year" -> {
                            // No year chosen yet: filter on the most recent available one
                            LaunchedEffect(availableYears) {
                                if (year == null) {
                                    availableYears.lastOrNull()?.let { lastYear ->
                                        year = lastYear
                                        applyFilter(selectionFilter("year"))
                                    }
                                }
                            }
                            DateSelection(
                                year = year,
                                availableYears = availableYears,
                                onYearChange = { lyear ->
                                    year = lyear
                                    applyFilter(selectionFilter("year"))
                                },
                                modifier = Modifier.weight(1f),
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        "type" -> {
                            EnumDropdownField(
                                selectedEnum = selectedWineType,
                                enumClass = WineType::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineType = enumValue as WineType
                                    applyFilter(selectionFilter("type"))
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner type...",
                                invisibleBorder = true,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        "format" -> {
                            EnumDropdownField(
                                selectedEnum = selectedWineFormat,
                                enumClass = WineFormat::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineFormat = enumValue as WineFormat
                                    applyFilter(selectionFilter("format"))
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner format...",
                                invisibleBorder = true,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        "region" -> {
                            EnumDropdownField(
                                selectedEnum = selectedWineRegion,
                                enumClass = WineRegion::class,
                                onSelectionChange = { displayName, enumValue ->
                                    selectedWineRegion = enumValue as WineRegion
                                    applyFilter(selectionFilter("region"))
                                },
                                modifier = Modifier.weight(1f),
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = "Sélectionner region...",
                                invisibleBorder = true,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        "wineId" -> {
                            WineDropdownList(
                                wineViewModel = wineViewModel,
                                selectedWine = selectedWine,
                                excludeWineId = excludeWineIds,
                                onSelectWine = { wine ->
                                    selectedWine = wine
                                    applyFilter(selectionFilter("wineId"))
                                },
                                modifier = Modifier.weight(1f),
                                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                                    applyFilter(selectionFilter(field))
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