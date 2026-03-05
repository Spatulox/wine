package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnumDropdownField(
    selectedEnum: Any?,
    enumClass: KClass<out Enum<*>>,
    onSelectionChange: (String, Any) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String = "Sélectionner...",
    defaultValue: Any? = null,
    invisibleBorder: Boolean = false,
    modifier: Modifier = Modifier
) {
    val enumEntries = enumClass.java.enumConstants ?: emptyArray()

    fun getDisplayName(enumEntry: Any): String = when (enumEntry) {
        is WineType -> enumEntry.displayName
        is WineFormat -> enumEntry.displayName
        is BottlePosition -> enumEntry.displayName
        is ShelfInterleave -> enumEntry.displayName
        is WineRegion -> enumEntry.displayName
        else -> enumEntry.toString()
    }

    val displayValue = when {
        selectedEnum != null -> getDisplayName(selectedEnum)
        defaultValue != null -> getDisplayName(defaultValue)
        else -> placeholder
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayValue,//selectedEnum?.let { getDisplayName(it) } ?: placeholder,
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = if (invisibleBorder) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedBorderColor = if (invisibleBorder) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            enumEntries.forEach { enumEntry ->
                val displayName = getDisplayName(enumEntry)
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onExpandedChange(false)
                        onSelectionChange(displayName, enumEntry)
                    }
                )
            }
        }
    }
}