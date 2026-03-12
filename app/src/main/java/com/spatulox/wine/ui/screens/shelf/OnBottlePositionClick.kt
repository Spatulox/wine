package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.StockWithWine
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.wine.WineDropdownList
import com.spatulox.wine.ui.screens.wine.WineStar
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBottlePositionClick(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    position: Position,
    onDismiss: () -> Unit = {},
    onPlaceStock: (StockWithWine) -> Unit = { _ -> },
    onEditStock: (StockWithWine) -> Unit = { _ -> },
    onWithdraw: (Position, String) -> Unit = { _, _ -> },
    onDeleteStock: (Position) -> Unit = { _ -> }
) {

    var selectedWine by remember { mutableStateOf<Wine?>(null) }
    var reason by remember { mutableStateOf<String>("") }

    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val countStockedWine by stockViewModel.countWineIdStocked.collectAsStateWithLifecycle()
    val wineState by wineViewModel.wines.collectAsStateWithLifecycle()
    val currentStock = stockState[position]
    val currentWine = currentStock?.wine?.id?.let { wineState[it] }

    var isEditing by remember { mutableStateOf(false) }

    val excludeWineIds = countStockedWine.entries
        .filter { (wineId, count) ->
            val wine = wineState[wineId]
            count == 0 || (wine != null && count >= wine.qte)
        }
        .map { it.key }


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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = currentWine?.name ?: "Ajouter",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    currentWine?.let { wine -> WineStar(wine = wine) }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    currentWine?.let { wine ->

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = wine.type.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${wine.year}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                wine.region?.displayName?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        var editedComment by remember { mutableStateOf(currentStock.comment ?: "") }

                        CommentCard(
                            comment = editedComment,
                            isEditing = isEditing,
                            onEditClick = { isEditing = true },
                            onCommentChange = { editedComment = it },
                            onSave = {
                                val stock: StockWithWine = currentStock.copy(
                                    comment = editedComment
                                )
                                onEditStock(stock)
                                isEditing = false
                            }
                        )

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

                            if(selectedWine != null){
                                OutlinedTextField(
                                    value = reason,
                                    onValueChange = { reason = it },
                                    label = { Text("Commentaire") },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
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
                                    comment = reason,
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
                                onWithdraw(position, reason)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isEditing
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



@Composable
fun CommentCard(
    comment: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                )
            } else {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    text = if(comment.isEmpty()) "Pas de commentaire" else comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        comment.isEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface  // couleur normale
                    }
                )
            }

            IconButton(
                onClick = if (isEditing) onSave else onEditClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Sauvegarder" else "Modifier",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
