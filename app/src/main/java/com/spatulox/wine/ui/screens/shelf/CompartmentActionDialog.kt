package com.spatulox.wine.ui.screens.shelf

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.spatulox.wine.SnackbarManager
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.send
import com.spatulox.wine.ui.screens.components.BottleGrid
import com.spatulox.wine.ui.screens.components.EnumDropdownField
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompartmentActionDialog(
    navController: NavController,
    compartmentViewModel: CompartmentViewModel,
    shelfViewModel: ShelfViewModel,
    stockViewModel: StockViewModel,
    compartmentId: String? = null
) {

    val compartment by compartmentViewModel.compartments.collectAsStateWithLifecycle()
    val stock by stockViewModel.stockByShelfId.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }


    // État lignes
    var shelves by remember { mutableStateOf(listOf<Shelf>()) }
    val shelfOrder by remember(shelves) { derivedStateOf { shelves.lastOrNull()?.order?.let { it + 1 } ?: 0 } }
    val compOrder by remember(compartment) { derivedStateOf { compartment.lastOrNull()?.order?.let { it + 1 } ?: 0 } }
    var showAddShelfDialog by remember { mutableStateOf(false) }
    var newShelfCols by remember { mutableStateOf("6") }
    var newShelfInterleaveExpanded by remember { mutableStateOf(false) }
    var newShelfBottleExpanded by remember { mutableStateOf(false) }
    var newShelfInterleave by remember { mutableStateOf<ShelfInterleave>(ShelfInterleave.STRAIGHT) }
    var newShelfBottlePosition by remember { mutableStateOf<BottlePosition>(BottlePosition.BASE) }


    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(compartmentId) {
        compartmentId?.toIntOrNull()?.let { id ->
            val compartment = compartmentViewModel.getCompartmentById(id)
            compartment?.let {
                name = it.name

                val localshelves = shelfViewModel.getShelvesByCompartmentId(id)
                localshelves?.let {
                    shelves = localshelves
                }

            }

        }
    }

    val duplicateError by remember(name, shelves.size) {
        derivedStateOf {
            if (shelves.isEmpty()) {
                "Vous ne pouvez pas avoir de compartiment vide, veuillez ajouter une ligne de rangement"
            } else {
                ""
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier
                    .imePadding()
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (compartmentId != null) "Modifier" else "Ajouter"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                actions = {
                    if (compartmentId != null) {
                        IconButton(
                            onClick = {
                                coroutine.launch {
                                    val id = compartmentId.toInt()
                                    val compartment = compartmentViewModel.getCompartmentById(id)
                                    compartment?.let {
                                        val res = compartmentViewModel.delete(compartment)
                                        if (res != null) {
                                            snackbarHostState.showSnackbar(res)
                                            return@launch
                                        }
                                        navController.popBackStack()
                                    }
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Supprimer le compartiment"
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            if (duplicateError.isNotBlank()) {
                Card(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = duplicateError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                placeholder = { Text("A1, Cave 1...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(24.dp))

            if (shelves.isNotEmpty()) {
                CompartmentPreview(
                    shelves = shelves,
                    onShelvesChanged = { newList -> shelves = newList },
                    onShelvesDelete = { shelf ->
                        println(stock)
                        stock[shelf.id]?.size?.let {
                            coroutine.launch {
                                snackbarHostState.showSnackbar("You can't delete this shelf since there is wine stocked inside...")
                            }
                            return@CompartmentPreview
                        }
                        shelves = shelves - shelf
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = { showAddShelfDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ajouter une ligne")
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

                        val compartment = Compartment(
                            id = compartmentId?.toInt() ?: 0,
                            name = name,
                            order = compOrder
                        )

                        compartmentId?.let {
                            coroutine.launch {
                                compartmentViewModel.update(compartment, shelves)
                                navController.popBackStack()
                            }
                            return@Button
                        }

                        coroutine.launch {
                            compartmentViewModel.insert(compartment, shelves)
                            navController.popBackStack()
                        }
                    },
                    enabled = name.isNotBlank() && shelves.isNotEmpty()
                ) {
                    Text(if (compartmentId != null) "Mettre à jour" else "Créer compartiment")
                }
            }
        }
    }

    // DIALOG Ajout ligne
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
                    Text("Nouvelle ligne", style = MaterialTheme.typography.titleMedium)

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
                                    compartmentId = compartmentId?.toInt() ?: 0,
                                    col = colCount,
                                    aligment = newShelfInterleave,
                                    arrangement = newShelfBottlePosition,
                                    name = "",
                                    order = shelfOrder
                                )
                                shelves += shelf
                                showAddShelfDialog = false
                            },
                            enabled = newShelfCols.toIntOrNull() != null
                        ) {
                            Text("Ajouter ligne")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompartmentPreview(
    shelves: List<Shelf>,
    onShelvesChanged: (List<Shelf>) -> Unit,
    onShelvesDelete: (Shelf) -> Unit,
    modifier: Modifier = Modifier
) {

    val controlColumnWidth = 48.dp
    val spacingVertical = 20.dp
    val buttonSizeLarge = 22.dp
    val buttonSizeDelete = 36.dp
    val rowHeight = 44.dp // is also bottleSize which is based on the buttonSizeLarge (two for up and down icons)
    val spacingHorizontal = 8.dp

    fun moveUp(index: Int) {
        if (index <= 0) return
        val mutable = shelves.toMutableList()
        val tmp = mutable[index - 1]
        mutable[index - 1] = mutable[index]
        mutable[index] = tmp
        onShelvesChanged(mutable.mapIndexed { i, shelf -> shelf.copy(order = i) })
    }

    fun moveDown(index: Int) {
        if (index >= shelves.lastIndex) return
        val mutable = shelves.toMutableList()
        val tmp = mutable[index + 1]
        mutable[index + 1] = mutable[index]
        mutable[index] = tmp
        onShelvesChanged(mutable.mapIndexed { i, shelf -> shelf.copy(order = i) })
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 3 COLUMNS : Menu | Ronds (LazyRow) | Delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    // COLUMN 1 : MENU PER SHELVES (fixe)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacingVertical),
                        modifier = Modifier.width(controlColumnWidth)
                    ) {
                        shelves.forEachIndexed { index, shelf ->
                            Column(
                                modifier = Modifier.size(rowHeight),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (index > 0) {
                                    IconButton(
                                        onClick = { moveUp(index) },
                                        modifier = Modifier.size(buttonSizeLarge)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowUp,
                                            contentDescription = "Monter",
                                        )
                                    }
                                }
                                if (index < shelves.lastIndex) {
                                    IconButton(
                                        onClick = { moveDown(index) },
                                        modifier = Modifier.size(buttonSizeLarge)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Descendre",
                                        )
                                    }
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.width(spacingHorizontal))

                    // COLUMN 2 : BOTTLE
                    BottleGrid(
                        shelves = shelves,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        bottleSize = rowHeight,
                        verticalSpacing = spacingVertical,
                        onPositionClick = {},
                    )

                    Spacer(modifier = Modifier.width(spacingHorizontal))

                    // COLUMN 3 : DELETE PER SHELVES (fixe)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacingVertical),
                        modifier = Modifier.width(controlColumnWidth),
                    ) {
                        shelves.forEach { shelf ->
                            Column(
                                modifier = Modifier.size(rowHeight),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                IconButton(
                                    onClick = { onShelvesDelete(shelf) },
                                    modifier = Modifier.size(buttonSizeDelete),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

