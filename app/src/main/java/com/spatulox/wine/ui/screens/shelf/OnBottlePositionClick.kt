package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.ConfirmDialog
import com.spatulox.wine.ui.screens.wine.WineDropdownList
import com.spatulox.wine.ui.screens.wine.WineStar
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnBottlePositionClick(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    position: Position,
    onDismiss: () -> Unit = {},
    onPlaceStock: (StockWithWine) -> Unit = { _ -> },
    onWithdraw: (Position) -> Unit = { _ -> },
    onDeleteStock: (Position) -> Unit = { _ -> }
) {

    var selectedWine by remember { mutableStateOf<Wine?>(null) }

    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val countStockedWine by stockViewModel.countWineIdStocked.collectAsStateWithLifecycle()
    val wineState by wineViewModel.wines.collectAsStateWithLifecycle()
    val currentStock = stockState[position]
    val currentWine = currentStock?.wine?.id?.let { wineState[it] }


    // Only wines with bottles left to rack can be placed (including wines with nothing racked yet)
    val excludeWineIds = wineState.values
        .filter { wine -> wine.qte - (countStockedWine[wine.id] ?: 0) <= 0 }
        .map { it.id }


    var showUnrackConfirm by remember { mutableStateOf(false) }
    if (showUnrackConfirm) {
        ConfirmDialog(
            title = "Retirer de l'emplacement ?",
            text = "La bouteille n'est pas bue : elle repasse dans les bouteilles à ranger.",
            confirmLabel = "Retirer",
            onConfirm = {
                showUnrackConfirm = false
                onDeleteStock(position)
                onDismiss()
            },
            onDismiss = { showUnrackConfirm = false }
        )
    }

    Dialog (
        onDismissRequest = onDismiss
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = currentWine?.name ?: "Ajouter",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    currentWine?.let { wine -> WineStar(wine = wine) }

                    if (currentStock != null) {
                        Row(
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { showUnrackConfirm = true }
                            ) {
                                Icon(Icons.Filled.Unarchive, "Retirer de l'emplacement")
                            }
                        }
                    }

                }

                // Only the content scrolls: the action buttons below always stay reachable
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {

                    currentWine?.let { wine ->

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Wraps instead of squeezing the type when the region name is long
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = wine.type.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${wine.year}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                                wine.region?.displayName?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        if (wine.comment.isNotBlank()) {
                            Text(
                                text = wine.comment,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 8,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                    }

                    when {
                        // CAS 1: Position VIDE → Placer vin
                        currentStock == null -> {
                            WineDropdownList(
                                wineViewModel = wineViewModel,
                                excludeWineId = excludeWineIds,
                                selectedWine = selectedWine,
                                onSelectWine = { wine -> selectedWine = wine }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    val hasActionButton = currentStock == null && selectedWine != null || currentStock != null

                    TextButton(
                        onClick = onDismiss,
                        modifier = if (hasActionButton) {
                            Modifier.weight(1f)
                        } else {
                            Modifier
                        }
                    ) {
                        Text("Annuler")
                    }

                    // Action principale (conditionnelle)
                    if (currentStock == null && selectedWine != null) {
                        Button(
                            onClick = {
                                val stock = StockWithWine(
                                    wine = selectedWine!!,
                                    position = position,
                                    comment = null,
                                    date = System.currentTimeMillis()
                                )
                                onPlaceStock(stock)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Text("Placer")
                        }
                    } else if (currentStock != null) {
                        Button(
                            onClick = {
                                onWithdraw(position)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Remove, null)
                            Text("Retirer")
                        }
                    }
                }
            }
        }
    }
}
