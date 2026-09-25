package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.spatulox.wine.SnackbarManager
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.navigation.Destinations
import com.spatulox.wine.send
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CompartmentScreen(
    stockViewModel: StockViewModel,
    wineViewModel: WineViewModel,
    shelfViewModel: ShelfViewModel,
    isEditing: Boolean,
    onEditingChange: (Boolean) -> Unit,
    compartmentViewModel: CompartmentViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val winesPositionMap by wineViewModel.filteredWinesMap.collectAsStateWithLifecycle()

    val compartment by compartmentViewModel.compartments.collectAsStateWithLifecycle()
    val shelvesByCompartment by shelfViewModel.shelvesByCompartmentId.collectAsStateWithLifecycle()

    val coroutine = rememberCoroutineScope()

    var positionClicked by remember { mutableStateOf<Position?>(null) }

    var draggedPosition by remember { mutableStateOf<Position?>(null) }
    var hoveredPosition by remember { mutableStateOf<Position?>(null) }
    var currentDragFingerPos by remember { mutableStateOf<Offset?>(null) }
    val positionBounds = remember { mutableStateMapOf<Position, Rect>() }

    fun resetDrag() {
        draggedPosition = null
        hoveredPosition = null
        currentDragFingerPos = null
    }

    // Always resets the drag state, whether the bottle is dropped on a free spot, an occupied
    // one or in the void
    fun endDrag() {
        val from = draggedPosition
        val to = hoveredPosition
        resetDrag()
        if (from == null || to == null || from == to) return
        val stock = stockState[from] ?: return
        coroutine.launch {
            if (stockState[to] != null || !stockViewModel.move(stock, to)) {
                SnackbarManager.send("Impossible de déplacer la bouteille ici : l'emplacement est déjà occupé")
            }
        }
    }

    // Entering or leaving the edit mode never inherits a previous drag
    LaunchedEffect(isEditing) {
        resetDrag()
    }

    val countStockedWine by stockViewModel.countWineIdStocked.collectAsStateWithLifecycle()

    val unrackedWines = remember(winesPositionMap, countStockedWine) {
        winesPositionMap.values
            .associate { wine -> wine.id to wine.qte - (countStockedWine[wine.id] ?: 0) }
            .filterValues { it > 0 }  // Seulement celles à ranger
    }

    val unrackedWinesCount = unrackedWines.values.sum()

    val unrackedLines = remember(unrackedWines) {
        unrackedWines.entries
            .sortedByDescending { it.value }
            .mapNotNull { (wineId, count) ->
                winesPositionMap[wineId]?.let { wine ->
                    "• ${wine.name} ${wine.year} (${wine.format.displayName}) : $count"
                }
            }
    }
    var isUnrackedExpanded by rememberSaveable { mutableStateOf(false) }


    fun moveUp(index: Int) {
        if (index <= 0) return
        val mutable = compartment.toMutableList()
        val tmp = mutable[index - 1]
        mutable[index - 1] = mutable[index]
        mutable[index] = tmp
        coroutine.launch {
            if (!compartmentViewModel.updateOrder(mutable.mapIndexed { i, comp -> comp.copy(order = i) })) {
                SnackbarManager.send("Impossible de changer l'ordre des compartiments")
            }
        }
    }

    fun moveDown(index: Int) {
        if (index >= compartment.lastIndex) return
        val mutable = compartment.toMutableList()
        val tmp = mutable[index + 1]
        mutable[index + 1] = mutable[index]
        mutable[index] = tmp
        coroutine.launch {
            if (!compartmentViewModel.updateOrder(mutable.mapIndexed { i, comp -> comp.copy(order = i) })) {
                SnackbarManager.send("Impossible de changer l'ordre des compartiments")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isEditing) {
            if (isEditing) {
                detectTapGestures(onTap = {
                    onEditingChange(false)
                })
            }
        }
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            // Part of the list (and collapsed by default) so a long list of wines to rack
            // can't push the cellar off-screen
            if (unrackedWinesCount > 0 && !isEditing) {
                item {
                    Card(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isUnrackedExpanded = !isUnrackedExpanded }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$unrackedWinesCount bouteille(s) à ranger",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isUnrackedExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = if (isUnrackedExpanded) "Masquer le détail" else "Afficher le détail"
                                )
                            }
                            if (isUnrackedExpanded) {
                                unrackedLines.forEach { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(compartment.size, key = { compartment[it].id }) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    if(isEditing){
                        Column() {
                            if(index > 0) {
                                IconButton(
                                    onClick = { moveUp(index) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowUp,
                                        contentDescription = "Monter",
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            if (index < compartment.lastIndex) {
                                IconButton(
                                    onClick = { moveDown(index) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Descendre",
                                    )
                                }
                            }
                        }
                    }

                    CompartmentView(
                        compartment = compartment[index],
                        shelves = shelvesByCompartment[compartment[index].id],
                        stock = stockState,
                        wines = winesPositionMap,
                        isParentEditing = isEditing,
                        positionBounds = positionBounds,
                        draggedPosition = draggedPosition,
                        hoveredPosition = hoveredPosition,
                        onFingerPositionUpdate = { newPos -> currentDragFingerPos = newPos },
                        onPositionDragStart = { position, _ ->
                            if (stockState[position] != null) {
                                draggedPosition = position
                                hoveredPosition = null
                            }
                        },
                        onPositionDragHover = { hoverPos ->
                            hoveredPosition = hoverPos
                        },
                        onDragEnd = { _ -> endDrag() },
                        onDragCancel = { resetDrag() },
                        onPositionClick = { position ->
                            if(!isEditing){
                                positionClicked = position
                            }
                        },
                        onEditClick = {
                            navController.navigate("${Destinations.COMPARTMENT_EDIT}/${compartment[index].id}")
                        }
                    )
                }
            }

            item {
                if(!isEditing){
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(Destinations.COMPARTMENT_ADD)
                            },
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Ajouter compartiment",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if(isEditing && draggedPosition != null && currentDragFingerPos != null){
        Card(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Text(
                text = "Déplacement…",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }


    if (!isEditing && positionClicked != null) {
            OnBottlePositionClick(
                wineViewModel = wineViewModel,
                stockViewModel = stockViewModel,
                position = positionClicked!!,
                onPlaceStock = {stock ->
                    coroutine.launch { stockViewModel.insert(stock) }
                },
                onEditStock = {stock ->
                    coroutine.launch { stockViewModel.update(stock) }
                },
                onWithdraw = {position, comment ->
                    coroutine.launch { stockViewModel.withdraw(position, comment) }
                },
                onDeleteStock = {position ->
                    coroutine.launch { stockViewModel.delete(position) }
                },
                onDismiss = { positionClicked = null }
            )
    }
}