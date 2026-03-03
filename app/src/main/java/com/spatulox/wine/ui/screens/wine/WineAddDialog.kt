package com.spatulox.wine.ui.screens.wine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.DateSelection
import java.time.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineAddDialog(
    onDismiss: () -> Unit,
    onValidate: (Wine) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<WineType>(WineType.ROUGE) }
    var year by remember { mutableStateOf(LocalDate.now().year - 3) }
    var stars by remember { mutableStateOf(0) }
    var format by remember { mutableStateOf(WineFormat.BOTTLE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau vin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Nom
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du vin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                DateSelection(
                    year = year,
                    onYearChange = { localYear ->
                        year = localYear
                    }
                )

                // Format (dropdown)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = format.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Format") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        WineFormat.entries.forEach { wineFormat ->
                            DropdownMenuItem(
                                text = { Text(wineFormat.displayName) },
                                onClick = {
                                    format = wineFormat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                var expandedType by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = type.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        WineType.entries.forEach { wineType ->
                            DropdownMenuItem(
                                text = { Text(wineType.name) },
                                onClick = {
                                    type = wineType
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // Étoiles (slider)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Note")
                        Text("$stars / 5 ⭐")
                    }
                    Slider(
                        value = stars.toFloat(),
                        onValueChange = { stars = it.roundToInt().coerceIn(0, 5) },
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
                    if (name.isNotBlank()) {
                        onValidate(
                            Wine(
                                name = name,
                                year = year,
                                format = format,
                                type = type,
                                stars = stars
                            )
                        )
                    }
                }
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}