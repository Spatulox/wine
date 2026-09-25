package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    // The text stays free while typing (e.g. empty, or below the minimum on the way to a bigger
    // number); it is only clamped when the field loses focus
    var textFieldValue by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        if (textFieldValue.toIntOrNull() != value) {
            textFieldValue = value.toString()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        label?.let {
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp, start = 0.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bouton Moins
            IconButton(
                onClick = {
                    val newValue = (value - step).coerceIn(minValue, maxValue)
                    onValueChange(newValue)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Diminuer")
            }

            // TextField
            OutlinedTextField(
                value = textFieldValue,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                onValueChange = { text ->
                    val cleaned = text.filter { it.isDigit() }.take(9)
                    val parsed = cleaned.toIntOrNull()
                    if (parsed == null || parsed <= maxValue) {
                        textFieldValue = cleaned
                        parsed?.let(onValueChange)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                modifier = Modifier
                    .width(70.dp)
                    .onFocusChanged { focus ->
                        if (!focus.isFocused) {
                            val clamped = (textFieldValue.toIntOrNull() ?: value).coerceIn(minValue, maxValue)
                            textFieldValue = clamped.toString()
                            if (clamped != value) onValueChange(clamped)
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )

            // Bouton Plus
            IconButton(
                onClick = {
                    val newValue = (value + step).coerceIn(minValue, maxValue)
                    onValueChange(newValue)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Augmenter")
            }
        }
    }
}
