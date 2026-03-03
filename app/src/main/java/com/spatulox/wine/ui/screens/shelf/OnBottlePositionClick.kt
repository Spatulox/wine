package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBottlePositionClick(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    position: Position,
    onDismiss: () -> Unit = {},
    onPlaceWine: (Int) -> Unit = {},
    onWithdraw: () -> Unit = {},
    onDeleteStock: () -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedWineId by remember { mutableStateOf<Int?>(null) }

    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val wineState by wineViewModel.winesByYear.collectAsStateWithLifecycle()

    val wines = wineState

    val currentStock = stockState[position]
    val currentWine = currentStock?.wineId?.let { wineState[it] }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Position ${position}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Position actuelle
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Étagère: ${position.shelf}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Ligne: ${position.row}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Colonne: ${position.col}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Vin actuel
                currentWine?.let { wine ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = wine.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${wine.year}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } ?: Text(
                    text = "Vide",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (currentStock == null) {
                    // Sélecteur de vin
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedWineId?.let { wines[it]?.name }
                                ?: "Sélectionner un vin...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Placer un vin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            wines.values.forEach { wine ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${wine.name} (${wine.year}, ${wine.format.displayName})",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        selectedWineId = wine.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                if (currentStock == null) {
                    selectedWineId?.let { wineId ->
                        TextButton(onClick = { onPlaceWine(wineId) }) {
                            Icon(Icons.Default.Check, null)
                            Text("Placer")
                        }
                    }
                } else {
                    TextButton(onClick = onDeleteStock) {
                        Icon(Icons.Filled.Delete, null)
                        Text("Supprimer")
                    }
                    TextButton(onClick = onWithdraw) {
                        Icon(Icons.Filled.Remove, null)
                        Text("Retirer")
                    }
                }

            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}