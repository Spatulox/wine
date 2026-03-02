package com.spatulox.wine.ui.screens.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.coroutineScope

data class Filter(
    val content: String,
    val field: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWithFilters(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOutsideClick: (() -> Unit)? = null
) {
    var isFilterPopupVisible by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedField by remember { mutableStateOf("name") }

    val filterFields = listOf(
        "name" to Icons.Filled.Person,
        "year" to Icons.Filled.DateRange,
        "format" to Icons.Filled.WineBar
    )

    // FAB (inchangé - row pleine largeur)
    FloatingActionButton(
        onClick = {
            onExpandedChange(!isExpanded)
            if (!isExpanded) {
                searchText = ""
                isFilterPopupVisible = false
                wineViewModel.clearFilter()
                stockViewModel.clearFilter()
                historyViewModel.clearFilter()
            }
        },
        modifier = modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.large
    ) {
        AnimatedContent(targetState = isExpanded, label = "fab_animation") { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f - (32f/392f))  // ← INCHANGÉ : pleine largeur
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { text ->
                            searchText = text
                            val filter = Filter(content = text, field = selectedField)
                            wineViewModel.updateFilter(filter)
                            stockViewModel.updateFilter(filter)
                            historyViewModel.updateFilter(filter)
                        },
                        placeholder = { Text("Rechercher...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

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

    // Popup filtres (seulement quand étendu)
    if (isFilterPopupVisible && isExpanded) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Filtres",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                filterFields.forEach { (field, icon) ->
                    FilterButton(
                        selected = selectedField == field,
                        icon = icon,
                        fieldName = field,
                        onClick = {
                            selectedField = field
                            val filter = Filter(content = searchText, field = field)
                            wineViewModel.updateFilter(filter)
                            stockViewModel.updateFilter(filter)
                            historyViewModel.updateFilter(filter)
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        isFilterPopupVisible = false
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
private fun FilterButton(
    selected: Boolean,
    icon: ImageVector,
    fieldName: String,
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (fieldName) {
                    "name" -> "Nom"
                    "year" -> "Année"
                    "format" -> "Format"
                    else -> fieldName
                },
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}