package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WineBar
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class IconItem(
    val name: String,
    val imageVector: ImageVector
)

@Composable
fun IconPicker(
    selectedIconName: String?,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val allIcons = remember { getAllFilledIcons() }

    val filteredIcons = remember(searchQuery, allIcons) {
        if (searchQuery.isEmpty()) {
            allIcons
        } else {
            allIcons.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = modifier.imePadding()
        ) {
            Column {
                // Barre de recherche
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Rechercher une icône") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                // Liste des icônes
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredIcons.chunked(4)) { iconRow ->
                        IconRow(icons = iconRow, selectedIconName = selectedIconName) {
                            onIconSelected(it.name)
                        }
                    }
                }

                // Boutons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                }
            }
        }
    }
}

@Composable
fun IconPickerButton(
    currentIconName: String,
    onIconChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showPicker = !showPicker },
        modifier = modifier
    ) {
        Icon(
            imageVector = IconFromName(currentIconName),
            contentDescription = if (showPicker) "Fermer sélection" else "Changer icône"
        )
    }

    if (showPicker) {
        IconPicker(
            selectedIconName = currentIconName,
            onIconSelected = { newIconName ->
                onIconChange(newIconName)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}


@Composable
private fun IconRow(
    icons: List<IconItem>,
    selectedIconName: String?,
    onIconClick: (IconItem) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        icons.forEach { icon ->
            IconItemCard(
                icon = icon,
                isSelected = selectedIconName == icon.name,
                onClick = { onIconClick(icon) }
            )
        }
    }
}

@Composable
private fun IconItemCard(
    icon: IconItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = icon.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun IconFromName(iconName: String?): ImageVector {
    return getAllFilledIcons()
        .find { it.name == iconName }
        ?.imageVector ?: Icons.Default.Star
}

// Map statique avec les icônes les plus utilisées
private val iconNameToVector = mapOf(
    "WineBar" to Icons.Default.WineBar,
    "LocalDrink" to Icons.Default.LocalDrink,
    "Liquor" to Icons.Default.Liquor,
    "LocalBar" to Icons.Default.LocalBar,
    "EmojiFoodBeverage" to Icons.Default.EmojiFoodBeverage,
    "Star" to Icons.Default.Star,
    "Favorite" to Icons.Default.Favorite,
    "Add" to Icons.Default.Add,
    "Edit" to Icons.Default.Edit,
    "Delete" to Icons.Default.Delete,
    // Ajoute ici tes icônes préférées
)

fun getAllFilledIcons(): List<IconItem> {
    return listOf(
        // 🍷 ICÔNES VIN / BOUTEILLES (top priorité)
        IconItem(Icons.Default.WineBar.name, Icons.Default.WineBar),
        IconItem(Icons.Default.LocalDrink.name, Icons.Default.LocalDrink),
        IconItem(Icons.Default.Liquor.name, Icons.Default.Liquor),
        IconItem(Icons.Default.LocalBar.name, Icons.Default.LocalBar),
        IconItem(Icons.Default.EmojiFoodBeverage.name, Icons.Default.EmojiFoodBeverage),

        // 🍾 BOUTEILLES / VERRES
        IconItem(Icons.Default.WaterDrop.name, Icons.Default.WaterDrop),
        IconItem(Icons.Default.LocalCafe.name, Icons.Default.LocalCafe),
        IconItem(Icons.Default.Restaurant.name, Icons.Default.Restaurant),
        IconItem(Icons.Default.Kitchen.name, Icons.Default.Kitchen),
        IconItem(Icons.Default.Fastfood.name, Icons.Default.Fastfood),

        // ⭐ QUALITÉ / ÉTOILES
        IconItem(Icons.Default.Star.name, Icons.Default.Star),
        IconItem(Icons.Default.StarBorder.name, Icons.Default.StarBorder),
        IconItem(Icons.Default.Grade.name, Icons.Default.Grade),
        IconItem(Icons.Default.Favorite.name, Icons.Default.Favorite),
        IconItem(Icons.Default.FavoriteBorder.name, Icons.Default.FavoriteBorder),

        // ✅ ACTIONS (CRUD)
        IconItem(Icons.Default.Add.name, Icons.Default.Add),
        IconItem(Icons.Default.Edit.name, Icons.Default.Edit),
        IconItem(Icons.Default.Delete.name, Icons.Default.Delete),
        IconItem(Icons.Default.Check.name, Icons.Default.Check),
        IconItem(Icons.Default.Close.name, Icons.Default.Close),

        // 🏠 NAVIGATION / INFO
        IconItem(Icons.Default.Home.name, Icons.Default.Home),
        IconItem(Icons.Default.Search.name, Icons.Default.Search),
        IconItem(Icons.Default.Settings.name, Icons.Default.Settings),
        IconItem(Icons.Default.Info.name, Icons.Default.Info),
        IconItem(Icons.Default.List.name, Icons.Default.List),

        // 📱 UI / ACTIONS
        IconItem(Icons.Default.Menu.name, Icons.Default.Menu),
        IconItem(Icons.Default.ShoppingCart.name, Icons.Default.ShoppingCart),
        IconItem(Icons.Default.Share.name, Icons.Default.Share),
        IconItem(Icons.Default.Visibility.name, Icons.Default.Visibility),
        IconItem(Icons.Default.ArrowBack.name, Icons.Default.ArrowBack),

        // 🎯 EXTRA (sympa pour cave à vin)
        IconItem(Icons.Default.Category.name, Icons.Default.Category),
        IconItem(Icons.Default.Inventory.name, Icons.Default.Inventory),
        IconItem(Icons.Default.Dashboard.name, Icons.Default.Dashboard),
        IconItem(Icons.Default.ConfirmationNumber.name, Icons.Default.ConfirmationNumber),
    ).sortedBy { it.name }
}

