package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ButtonColorPicker(
    currentColor: Color?,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val displayColor = currentColor ?: MaterialTheme.colorScheme.surface
    // Color being picked in the dialog, only applied on "OK"
    var pickedColor by remember(showDialog) { mutableStateOf(displayColor) }

    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(
                displayColor,
                CircleShape
            )
            .clickable { showDialog = true }
            .border(2.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Modifier couleur",
            tint = if (displayColor.luminance() > 0.5f) Color.Black else Color.White,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.Center)
        )
    }

    // Dialog avec ColorPicker
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            // Conteneur avec coins arrondis + padding
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Titre
                    Text(
                        text = "Choisir une couleur",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    // ColorPicker
                    ColorPicker(
                        type = ColorPickerType.Circle(
                            showBrightnessBar = true,
                            showAlphaBar = false,
                            lightCenter = true
                        ),
                        onPickedColor = {
                            pickedColor = it
                        }
                    )

                    // Boutons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text("Annuler")
                        }

                        TextButton(
                            onClick = {
                                onColorChange(pickedColor)
                                showDialog = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
