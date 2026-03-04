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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.wine.WineDropdownList
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBottlePositionClick(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    position: Position,
    onDismiss: () -> Unit = {},
    onPlaceWine: (Position, Wine, String) -> Unit = { _, _, _ -> },
    onWithdraw: (Position, String) -> Unit = { _, _ -> },
    onDeleteStock: (Position) -> Unit = { _ -> }
) {

    var selectedWine by remember { mutableStateOf<Wine?>(null) }
    var reason by remember { mutableStateOf<String>("") }

    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val wineState by wineViewModel.wines.collectAsStateWithLifecycle()
    val currentStock = stockState[position]
    val currentWine = currentStock?.wineId?.let { wineState[it] }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = " ${if (currentStock == null) "Placer" else "Enlever" }",
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

                when {
                    // ✅ CAS 1: Position VIDE → Placer vin
                    currentStock == null -> {
                        WineDropdownList(
                            wineViewModel = wineViewModel,
                            selectedWine = selectedWine,
                            onSelectWine = { wine -> selectedWine = wine }
                        )
                    }

                    // ✅ CAS 2: Position OCCUPÉE → Retrait/Supprimer
                    else -> {
                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            label = { Text("Raison du retrait") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                }
            }
        },
        confirmButton = {
            // ✅ UNIQUEMENT quand vide + vin sélectionné
            if (currentStock == null && selectedWine != null) {
                TextButton(onClick = {
                    onPlaceWine(position, selectedWine!!, reason)
                    onDismiss()
                }) {
                    Icon(Icons.Default.Check, null)
                    Text("Placer")
                }
            } else if(currentStock != null) {
                TextButton(
                    onClick = {
                        onWithdraw(position, reason)
                        onDismiss()
                    }
                ) {
                    Icon(Icons.Filled.Remove, null)
                    Text("Retirer")
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