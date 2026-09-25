package com.spatulox.wine.ui.screens.wine

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.ButtonColorPicker
import com.spatulox.wine.ui.screens.components.DateSelection
import com.spatulox.wine.ui.screens.components.EnumDropdownField
import com.spatulox.wine.ui.screens.components.NumberField
import com.spatulox.wine.ui.screens.components.PriceField
import com.spatulox.wine.ui.screens.components.parsePrice
import com.spatulox.wine.viewModels.WineViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WineAddDialog(
    wineViewModel: WineViewModel,
    onDismiss: () -> Unit,
    onValidate: (Wine) -> Unit,
    saveError: String? = null
) {
    var name by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf<WineType>(WineType.ROUGE) }
    var year by rememberSaveable { mutableStateOf(LocalDate.now().year - 2) }
    var stars by rememberSaveable { mutableStateOf(0) }
    var format by rememberSaveable { mutableStateOf(WineFormat.BOTTLE) }
    var qte by rememberSaveable { mutableStateOf(6) }
    var region by rememberSaveable { mutableStateOf<WineRegion?>(null) }
    var priceText by rememberSaveable { mutableStateOf("") }
    var wineColor by remember { mutableStateOf<Color?>(DEFAULT_WINE_COLOR) }
    var comment by rememberSaveable { mutableStateOf("") }

    val wines by wineViewModel.wines.collectAsStateWithLifecycle()
    // Same fields and case sensitivity as the unique (name, year, format) index
    val isDuplicate by remember(name, year, format, wines) {
        derivedStateOf {
            wines.values.any { existing ->
                existing.name == name.trim() &&
                        existing.year == year &&
                        existing.format == format
            }
        }
    }
    val errorMessage = saveError
        ?: if (isDuplicate) "Ce vin existe déjà (même nom, année et format)" else ""

    Dialog(
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
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top= 24.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ButtonColorPicker(
                    currentColor = wineColor,
                    onColorChange = { wineColor = it }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Nouveau vin",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            LazyColumn (
                modifier = Modifier.padding(24.dp).imePadding(),
            ) {
                item {

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

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Commentaire") },
                            modifier = Modifier.fillMaxWidth(),
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
                            label = "Nombre de bouteilles"
                        )

                        PriceField(
                            value = priceText,
                            onValueChange = { priceText = it },
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
                                                unitPrice = parsePrice(priceText),
                                                stars = stars,
                                                qte = qte,
                                                region = region,
                                                color = wineColor,
                                                comment = comment
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = name.isNotBlank() && !isDuplicate
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

// Fixed default instead of the (dynamic) theme color, so the stored color doesn't depend on the wallpaper
private val DEFAULT_WINE_COLOR = Color(0xFF8E1B3A)
