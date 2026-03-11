package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.ui.screens.components.EnumDropdownField
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel


enum class ShelfActionType {
    ADD_UPDATE,
    DELETE
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompartmentActionDialog(
    navController: NavController,
    compartmentViewModel: CompartmentViewModel,
    shelfViewModel: ShelfViewModel
) {
    var name by remember { mutableStateOf("") }


    // État étagères
    var shelves by remember { mutableStateOf(listOf<Shelf>()) }
    var showAddShelfDialog by remember { mutableStateOf(false) }
    var newShelfCols by remember { mutableStateOf("6") }
    var newShelfInterleaveExpanded by remember { mutableStateOf(false) }
    var newShelfBottleExpanded by remember { mutableStateOf(false) }
    var newShelfInterleave by remember { mutableStateOf<ShelfInterleave?>(ShelfInterleave.STRAIGHT) }
    var newShelfBottlePosition by remember { mutableStateOf<BottlePosition?>(BottlePosition.BASE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter Compartiment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {


            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nom") }, placeholder = { Text("A1, Cave 1...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (shelves.isNotEmpty()) {
                CompartmentPreview(
                    shelves = shelves,
                    onDeleteShelf = { shelf -> shelves = shelves - shelf },
                    onMenuClick = {}
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = { showAddShelfDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ajouter une étagère")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Boutons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Annuler")
                }
                Button(
                    onClick = {
                        // Logique ajout compartiment + étagères
                        navController.popBackStack()
                    },
                    enabled = name.isNotBlank() && shelves.isNotEmpty()
                ) {
                    Text("Créer compartiment")
                }
            }
        }
    }

    // DIALOG Ajout étagère
    if (showAddShelfDialog) {
        Dialog(onDismissRequest = { showAddShelfDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Nouvelle étagère", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = newShelfCols,
                        onValueChange = { newShelfCols = it.filter { c -> c.isDigit() } },
                        label = { Text("Colonnes") },
                        placeholder = { Text("6") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    EnumDropdownField(
                        selectedEnum = newShelfInterleave,
                        enumClass = ShelfInterleave::class,
                        onSelectionChange = { _, enumEntry ->
                            newShelfInterleave = enumEntry as ShelfInterleave
                        },
                        expanded = newShelfInterleaveExpanded,
                        onExpandedChange = { newShelfInterleaveExpanded = it },
                        placeholder = "Alignement...",
                        modifier = Modifier.fillMaxWidth()
                    )

                    EnumDropdownField(
                        selectedEnum = newShelfBottlePosition,
                        enumClass = BottlePosition::class,
                        onSelectionChange = { _, enumEntry ->
                            newShelfBottlePosition = enumEntry as BottlePosition
                        },
                        expanded = newShelfBottleExpanded,
                        onExpandedChange = { newShelfBottleExpanded = it },
                        placeholder = "Position...",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddShelfDialog = false }) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val colCount = newShelfCols.toIntOrNull() ?: 6
                                val shelf = Shelf(
                                    id = 0,
                                    compartmentId = 0,
                                    col = colCount,
                                    aligment = newShelfInterleave ?: ShelfInterleave.STRAIGHT,
                                    arrangement = newShelfBottlePosition ?: BottlePosition.BASE,
                                    name = "",
                                )
                                shelves += shelf
                                showAddShelfDialog = false
                            },
                            enabled = newShelfCols.toIntOrNull() != null &&
                                    newShelfInterleave != null &&
                                    newShelfBottlePosition != null
                        ) {
                            Text("Ajouter étagère")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompartmentPreview(
    shelves: List<Shelf>,
    onMenuClick: (Shelf) -> Unit,
    onDeleteShelf: (Shelf) -> Unit,
    modifier: Modifier = Modifier
) {

    val maxCols = shelves.maxOfOrNull { it.col } ?: 6

    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 3 COLUMNS : Menu | Ronds (LazyRow) | Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // COLUMN 1 : MENU PER SHELVES (fixe)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.width(48.dp)
                ) {
                    repeat(shelves.size) { shelfIndex ->
                        IconButton(
                            onClick = { onMenuClick(shelves[shelfIndex]) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // COLUMN 2 : BOTTLE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(maxCols) { colIndex ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                shelves.forEach { shelf ->
                                    if (colIndex < shelf.col) {
                                        val offset = when (shelf.aligment) {
                                            ShelfInterleave.STRAIGHT -> 0.dp
                                            ShelfInterleave.STAGGERED_LEFT -> ((-22).dp)
                                            ShelfInterleave.STAGGERED_RIGHT -> 22.dp
                                        }
                                        Box(
                                            modifier = Modifier
                                                .offset(x = offset)
                                                .size(32.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                    CircleShape
                                                )
                                                .border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.outlineVariant,
                                                    CircleShape
                                                )
                                        )
                                    }  else {
                                        // INVISIBLES bottle to align
                                        Box(
                                            modifier = Modifier
                                                .offset(x = 0.dp)
                                                .size(32.dp)
                                                .background(
                                                    Color.Transparent,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // COLUMN 3 : DELETE PER SHELVES (fixe)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.width(48.dp)
                ) {
                    repeat(shelves.size) { shelfIndex ->
                        IconButton(
                            onClick = { onDeleteShelf(shelves[shelfIndex]) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}





/*@Composable
fun CompartmentActionDialog(
    compartment: Compartment? = null,
    onDismiss: () -> Unit,
    onAction: (ShelfActionType, Shelf) -> Unit,
    modifier: Modifier = Modifier
) {

    /*var interleaveExpanded by remember { mutableStateOf(false) }
    var bottleExpanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(compartment?.name ?:"") }
    var cols by remember { mutableStateOf(shelf?.col?.toString() ?: "6") }
    var selectedInterleave by remember { mutableStateOf<ShelfInterleave?>(compartment?.aligment) }
    var selectedBottlePosition by remember { mutableStateOf<BottlePosition?>(compartment?.arrangement) }

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
                                compartmentId = shelf?.compartmentId ?: 0,
                                name = name,
                                col = cols.toIntOrNull() ?: 6,
                                aligment = selectedInterleave ?: ShelfInterleave.STRAIGHT,
                                arrangement = selectedBottlePosition ?: BottlePosition.BASE
                            )
                            onAction(ShelfActionType.ADD_UPDATE, shelf)
                            onDismiss()
                        },
                        enabled = name.isNotBlank() &&
                                cols.toIntOrNull() != null
                    ) {
                        Text(shelf?.let { "Modifier" } ?: "Ajouter")
                    }
                }
            }
        }
    }*/
}
*/