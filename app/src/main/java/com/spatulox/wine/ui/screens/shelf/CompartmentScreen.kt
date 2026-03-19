package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.background
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
    var endOfDrag by remember { mutableStateOf<Boolean>(false) }
    val rectBounds = remember { mutableStateMapOf<Rect, Position>() }
    val positionBounds = remember { mutableStateMapOf<Position, Rect>() }

    LaunchedEffect(isEditing) {
        if (!isEditing) {
            draggedPosition = null
            hoveredPosition = null
        }
    }

    val unrackedWines = remember(winesPositionMap, stockState) {
        winesPositionMap.values
            .filter { it.qte > 0 }  // Vins présents
            .associate { wine ->
                val wineId = wine.id
                val totalBottles = wine.qte  // Total à placer
                val stockedBottles = stockState.values.count { it.wine.id == wineId }  // Déjà rangés
                wineId to (totalBottles - stockedBottles).coerceAtLeast(0)
            }
            .filterValues { it > 0 }  // Seulement celles à ranger
    }

    val unrackedWinesCount = unrackedWines.values.sum()

    val errorMessage by remember(unrackedWines) {
        mutableStateOf(
            if (unrackedWinesCount > 0) {
                buildString {
                    append("$unrackedWinesCount bouteille(s) à ranger :")
                    unrackedWines.entries
                        .sortedByDescending { it.value }
                        .forEach { (wineId, count) ->
                            val wine = winesPositionMap[wineId]!!
                            append("\n• ${wine.name} ${wine.year} (${wine.format.displayName}) : $count")
                        }
                }
            } else {
                ""
            }
        )
    }


    fun moveUp(index: Int) {
        if (index <= 0) return
        val mutable = compartment.toMutableList()
        val tmp = mutable[index - 1]
        mutable[index - 1] = mutable[index]
        mutable[index] = tmp
        coroutine.launch {
            compartmentViewModel.updateOrder(mutable.mapIndexed { i, comp -> comp.copy(order = i) })
        }
    }

    fun moveDown(index: Int) {
        if (index >= compartment.lastIndex) return
        val mutable = compartment.toMutableList()
        val tmp = mutable[index + 1]
        mutable[index + 1] = mutable[index]
        mutable[index] = tmp
        coroutine.launch {
            compartmentViewModel.updateOrder(mutable.mapIndexed { i, comp -> comp.copy(order = i) })
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

        if (errorMessage.isNotBlank() && !isEditing) {
            Card(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(compartment.size) { index ->
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
                        rectBounds = rectBounds,
                        positionBounds = positionBounds,
                        draggedPosition = draggedPosition,
                        hoveredPosition = hoveredPosition,
                        onFingerPositionUpdate = { newPos -> currentDragFingerPos = newPos },
                        onPositionDragStart = { position, _ ->
                            endOfDrag = false
                            if (stockState[position] != null) {
                                draggedPosition = position
                                hoveredPosition = null
                            }
                        },
                        onPositionDragHover = { hoverPos ->
                            hoveredPosition = hoverPos
                        },
                        onDragEnd = { _ ->
                            endOfDrag = true
                        },
                        onDragCancel = {
                            draggedPosition = null
                            hoveredPosition = null
                        },
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
                text = "Moving",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }


    if(isEditing && draggedPosition != null && currentDragFingerPos != null) {
        /*Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        currentDragFingerPos!!.x.roundToInt(),
                        currentDragFingerPos!!.y.roundToInt()
                    )
                }
                .size(16.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    CircleShape
                )
                .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
        )*/
    }


    if (isEditing && endOfDrag && draggedPosition != null && hoveredPosition != null) {

        if(stockState[hoveredPosition] != null) {
            hoveredPosition = null
            draggedPosition = null
            return
        }
        MoveBottleDialog(
            from = draggedPosition!!,
            to = hoveredPosition!!,
            stockState = stockState,
            onMove = { from, to ->
                coroutine.launch {
                    val stock = stockState[from]
                    stock?.let {
                        if(stockState[to] != null){
                            SnackbarManager.send("You can't move the bottle here, there is already another one...")
                            return@let
                        }
                        stockViewModel.delete(from)
                        stockViewModel.insert(it.copy(position = to))
                    }
                }
                // Reset état
                draggedPosition = null
                hoveredPosition = null
            },
            onCancel = {
                draggedPosition = null
                hoveredPosition = null
            }
        )
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
                    coroutine.launch {
                        val wine = wineViewModel.getWineByPos(position)
                        if(wine != null){
                            stockViewModel.withdraw(position, comment)
                            wineViewModel.withdrawWine(wine)
                        }
                    }
                },
                onDeleteStock = {position ->
                    coroutine.launch { stockViewModel.delete(position) }
                },
                onDismiss = { positionClicked = null }
            )
    }
}