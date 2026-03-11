package com.spatulox.wine.ui.screens.wine

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.DateSelection
import com.spatulox.wine.ui.screens.components.EnumDropdownField
import com.spatulox.wine.ui.screens.components.IconFromName
import com.spatulox.wine.ui.screens.components.IconPicker
import com.spatulox.wine.ui.screens.components.IconPickerButton
import com.spatulox.wine.ui.screens.components.NumberField
import com.spatulox.wine.viewModels.WineViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineAddDialog(
    wineViewModel: WineViewModel,
    onDismiss: () -> Unit,
    onValidate: (Wine) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<WineType>(WineType.ROUGE) }
    var year by remember { mutableStateOf(LocalDate.now().year - 3) }
    var stars by remember { mutableStateOf(0) }
    var format by remember { mutableStateOf(WineFormat.BOTTLE) }
    var qte by remember { mutableStateOf(6) }
    val qteText by remember(qte) { derivedStateOf { qte.toString() } }
    var region by remember { mutableStateOf<WineRegion?>(null) }
    var unitPrice by remember { mutableStateOf<Float?>(null) }
    val priceText by remember(unitPrice) { derivedStateOf { unitPrice?.toString() ?: "" } }
    var currentIconName by remember { mutableStateOf<String>(Icons.Filled.Info.name) }

    var errorMessage by remember { mutableStateOf("") }
    val wines by wineViewModel.wines.collectAsStateWithLifecycle()
    val duplicateError by remember(name, year, type, format, wines) {
        derivedStateOf {
            wines.values.find { existing ->
                existing.name.equals(name.trim(), ignoreCase = true) &&
                        existing.year == year &&
                        existing.type == type &&
                        existing.format == format
            }?.let {
                "Duplicate Entry"
            } ?: ""
        }
    }
    LaunchedEffect(duplicateError) {
        errorMessage = duplicateError
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {

        Card(
            modifier = Modifier
            .fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            LazyColumn (
                modifier = Modifier.padding(24.dp).imePadding(),
            ) {
                item {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconPickerButton(
                            currentIconName = currentIconName,
                            onIconChange = { currentIconName = it }
                        )

                        Text(
                            text = "Nouveau vin",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMessage.isNotBlank()) {
                        Card(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

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

                        var expandedFormat by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = format,
                            enumClass = WineFormat::class,
                            onSelectionChange = { _, selectedFormat ->
                                format = selectedFormat as WineFormat
                            },
                            expanded = expandedFormat,
                            onExpandedChange = { expandedFormat = it },
                            placeholder = "Format"
                        )

                        var expandedType by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = type,
                            enumClass = WineType::class,
                            onSelectionChange = { _, selectedType ->
                                type = selectedType as WineType
                            },
                            expanded = expandedType,
                            onExpandedChange = { expandedType = it },
                            placeholder = "Type"
                        )

                        var expandedRegion by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = region,
                            enumClass = WineRegion::class,
                            onSelectionChange = { _, selectedRegion ->
                                region = selectedRegion as WineRegion
                            },
                            expanded = expandedRegion,
                            onExpandedChange = { expandedRegion = it },
                            placeholder = "Region"
                        )

                        NumberField(
                            modifier = Modifier.fillMaxWidth(),
                            value = qte,
                            onValueChange = { qte = it },
                            minValue = 0,
                            startValue = 6,
                            label = "Nombres de bouteilles :"
                        )

                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { text ->
                                val cleaned = text.filter { it.isDigit() || it == '.' }
                                val hasDot = cleaned.contains('.')
                                val dotParts = cleaned.split('.')

                                val validText = if (hasDot && dotParts[1].length > 2) {
                                    dotParts[0] + "." + dotParts[1].take(2)
                                } else cleaned

                                unitPrice = validText.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Prix unitaire (€)") },
                            prefix = { Text("€") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Annuler")
                            }
                            Button(
                                onClick = {
                                    if (name.isNotBlank()) {
                                        onValidate(
                                            Wine(
                                                name = name,
                                                year = year,
                                                format = format,
                                                type = type,
                                                unitPrice = unitPrice,
                                                stars = stars,
                                                qte = qte,
                                                region = region,
                                                icon = currentIconName
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ajouter")
                            }
                        }
                    }
                }
            }
        }
    }
}