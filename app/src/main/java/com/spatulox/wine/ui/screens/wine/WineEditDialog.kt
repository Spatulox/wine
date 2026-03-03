package com.spatulox.wine.ui.screens.wine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.model.Wine
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineEditDialog(
    wine: Wine,
    onDismiss: () -> Unit,
    onValidate: (Wine) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editedName by remember(wine) { mutableStateOf(wine.name) }
    var editedYear by remember(wine) { mutableStateOf(wine.year) }
    var editedStars by remember(wine) { mutableStateOf(wine.stars) }
    var editedFormat by remember(wine) { mutableStateOf(wine.format) }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Nom
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nom") },
                    singleLine = true
                )

                // Année (Number Picker)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { editedYear = (editedYear - 1).coerceAtLeast(1900) }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, null)
                    }
                    Text(
                        "${editedYear}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { editedYear = (editedYear + 1).coerceAtMost(2030) }) {
                        Icon(Icons.Filled.KeyboardArrowRight, null)
                    }
                }

                // Format (Dropdown)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = editedFormat.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Format") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        WineFormat.entries.forEach { format ->
                            DropdownMenuItem(
                                text = { Text(format.displayName) },
                                onClick = {
                                    editedFormat = format
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Étoiles (Slider)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Note (${editedStars}/5)")
                        Text("$editedStars ⭐")
                    }
                    Slider(
                        value = editedStars.toFloat(),
                        onValueChange = { editedStars = it.roundToInt().coerceIn(0, 5) },
                        valueRange = 0f..5f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onValidate(
                        wine.copy(
                            name = editedName,
                            year = editedYear,
                            format = editedFormat,
                            stars = editedStars
                        )
                    )
                }
            ) { Text("Valider") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) { Text("Supprimer") }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    )
}