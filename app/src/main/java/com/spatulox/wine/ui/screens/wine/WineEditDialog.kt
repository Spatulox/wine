package com.spatulox.wine.ui.screens.wine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.ButtonColorPicker
import com.spatulox.wine.ui.screens.components.DateSelection
import com.spatulox.wine.ui.screens.components.EnumDropdownField
import com.spatulox.wine.ui.screens.components.NumberField
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineEditDialog(
    wine: Wine,
    distincWineCounts: Map<Int, Int>,
    onDismiss: () -> Unit,
    onValidate: (Wine) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editedName by remember(wine) { mutableStateOf(wine.name) }
    var editedYear by remember(wine) { mutableStateOf(wine.year) }
    var editedType by remember { mutableStateOf<WineType>(wine.type) }
    var editedFormat by remember(wine) { mutableStateOf(wine.format) }
    var editedRegion by remember(wine) { mutableStateOf(wine.region) }
    var editedQte by remember(wine) { mutableStateOf(wine.qte) }
    var editedStars by remember(wine) { mutableStateOf(wine.stars) }
    var editedWineColor by remember { mutableStateOf<Color?>(wine.color) }

    var errorMessage by remember(editedQte, distincWineCounts[wine.id]) { mutableStateOf("") }

    Dialog (
        onDismissRequest = onDismiss,
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ButtonColorPicker(
                        currentColor = editedWineColor,
                        onColorChange = { editedWineColor = it }
                    )
                    Text(
                        text = "Modifier le vin",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer vin",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            LazyColumn (
                modifier = Modifier.padding(16.dp).imePadding()
            ) {

                item {

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Nom") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        DateSelection(
                            year = editedYear,
                            onYearChange = { editedYear = it }
                        )


                        var expandedFormat by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = editedFormat.displayName,
                            enumClass = WineFormat::class,
                            onSelectionChange = { _, selectedFormat ->
                                editedFormat = selectedFormat as WineFormat
                            },
                            expanded = expandedFormat,
                            onExpandedChange = { expandedFormat = it },
                            placeholder = "Format"
                        )

                        var expandedType by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = editedType,
                            enumClass = WineType::class,
                            onSelectionChange = { _, selectedType ->
                                editedType = selectedType as WineType
                            },
                            expanded = expandedType,
                            onExpandedChange = { expandedType = it },
                            placeholder = "Type"
                        )

                        var expandedRegion by remember { mutableStateOf(false) }
                        EnumDropdownField(
                            selectedEnum = editedRegion,
                            enumClass = WineRegion::class,
                            onSelectionChange = { _, selectedRegion ->
                                editedRegion = selectedRegion as WineRegion
                            },
                            expanded = expandedRegion,
                            onExpandedChange = { expandedRegion = it },
                            placeholder = "Region"
                        )

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

                        NumberField(
                            modifier = Modifier.fillMaxWidth(),
                            value = editedQte,
                            onValueChange = { qte ->
                                editedQte = qte
                                val currentStockCount = distincWineCounts[wine.id] ?: 0
                                if (qte <= currentStockCount) {
                                    errorMessage =
                                        "Impossible de mettre moins que le stock actuel rangé dans la cave ($currentStockCount)"
                                } else {
                                    errorMessage = ""
                                }
                            },
                            minValue = distincWineCounts[wine.id] ?: 0,
                            startValue = wine.qte,
                            label = "Nombres de bouteilles :"
                        )

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
                                onValidate(
                                    wine.copy(
                                        name = editedName,
                                        year = editedYear,
                                        type = editedType,
                                        format = editedFormat,
                                        region = editedRegion,
                                        qte = editedQte,
                                        stars = editedStars,
                                        color = editedWineColor
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Valider")
                        }
                    }
                }
            }
        }
    }
}