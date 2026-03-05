package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.ui.screens.components.EnumDropdownField


enum class ShelfActionType {
    ADD_UPDATE,
    DELETE
}

@Composable
fun ShelfActionDialog(
    shelf: Shelf? = null,
    onDismiss: () -> Unit,
    onAction: (ShelfActionType, Shelf) -> Unit,
    modifier: Modifier = Modifier
) {

    var interleaveExpanded by remember { mutableStateOf(false) }
    var bottleExpanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(shelf?.name ?:"") }
    var rows by remember { mutableStateOf( shelf?.rows?.toString() ?: "5") }
    var cols by remember { mutableStateOf(shelf?.cols?.toString() ?: "6") }
    var selectedInterleave by remember { mutableStateOf<ShelfInterleave?>(shelf?.interleave) }
    var selectedBottlePosition by remember { mutableStateOf<BottlePosition?>(shelf?.bottlePositionning) }

    val coroutineScope = rememberCoroutineScope()

    Dialog (
        onDismissRequest = onDismiss,
    ) {
        Card (
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom= 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shelf?.let { "Modifier ${shelf.name}" } ?: "Nouveau compartiment",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    shelf?.let { shelf ->
                        IconButton(
                            onClick = {
                                onAction(ShelfActionType.DELETE, shelf)
                                onDismiss()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer compartiment",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }


                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom") },
                        placeholder = { Text("A1, Cave 1, etc.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = rows,
                        onValueChange = { rows = it.filter { c -> c.isDigit() } },
                        label = { Text("Nombre de rangées") },
                        placeholder = { Text("5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cols,
                        onValueChange = { cols = it.filter { c -> c.isDigit() } },
                        label = { Text("Nombre de colonnes") },
                        placeholder = { Text("6") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Arrangement :")

                    EnumDropdownField(
                        selectedEnum = selectedInterleave,
                        enumClass = ShelfInterleave::class,
                        onSelectionChange = { _, enumEntry ->
                            selectedInterleave = enumEntry as ShelfInterleave
                        },
                        expanded = interleaveExpanded,
                        onExpandedChange = { interleaveExpanded = it },
                        placeholder = "Interleave...",
                        defaultValue = ShelfInterleave.STRAIGHT.displayName,
                        modifier = Modifier.fillMaxWidth()
                    )

                    EnumDropdownField(
                        selectedEnum = selectedBottlePosition,
                        enumClass = BottlePosition::class,
                        onSelectionChange = { _, enumEntry ->
                            selectedBottlePosition = enumEntry as BottlePosition
                        },
                        expanded = bottleExpanded,
                        onExpandedChange = { bottleExpanded = it },
                        placeholder = "Position...",
                        defaultValue = BottlePosition.BASE.displayName,
                        modifier = Modifier.fillMaxWidth()
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    val buttonModifier = Modifier
                    TextButton(
                        onClick = onDismiss,
                        modifier = buttonModifier
                    ) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = buttonModifier,
                        onClick = {
                            val shelf = Shelf(
                                id = shelf?.id ?: 0,
                                name = name,
                                rows = rows.toIntOrNull() ?: 5,
                                cols = cols.toIntOrNull() ?: 6,
                                interleave = selectedInterleave ?: ShelfInterleave.STRAIGHT,
                                bottlePositionning = selectedBottlePosition ?: BottlePosition.BASE
                            )
                            onAction(ShelfActionType.ADD_UPDATE, shelf)
                            onDismiss()
                        },
                        enabled = name.isNotBlank() &&
                                rows.toIntOrNull() != null &&
                                cols.toIntOrNull() != null
                    ) {
                        Text(shelf?.let { "Modifier" } ?: "Ajouter")
                    }
                }
            }
        }
    }
}
