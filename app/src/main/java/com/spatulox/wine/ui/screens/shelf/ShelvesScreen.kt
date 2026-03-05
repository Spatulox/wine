package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.SnackbarManager
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.send
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.launch

@Composable
fun ShelfScreen(
    stockViewModel: StockViewModel,
    wineViewModel: WineViewModel,
    shelfViewModel: ShelfViewModel,
    modifier: Modifier = Modifier,
    onPositionClick: (position: Position) -> Unit = { _ -> }
) {
    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val winesPositionMap by wineViewModel.filteredWinesMap.collectAsStateWithLifecycle()
    val shelves by shelfViewModel.shelves.collectAsStateWithLifecycle()

    var coroutine = rememberCoroutineScope()

    var positionClicked by remember { mutableStateOf<Position?>(null) }
    var editShelfClicked by remember { mutableStateOf<Shelf?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box (
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(shelves.size) { index ->
                ShelfView(
                    shelf = shelves[index],
                    stock = stockState,
                    wines = winesPositionMap,
                    onPositionClick = { position -> positionClicked = position },
                    onEditClick = {
                        showEditDialog = true
                        editShelfClicked = shelves[index]
                    }
                )
            }

            item {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Ajouter étagère",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }

    if(showAddDialog){
        ShelfActionDialog(
            onAction = { type, shelf ->
                coroutine.launch {
                    shelfViewModel.insert(shelf)
                }
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if(showEditDialog){

        System.out.println(editShelfClicked)

        ShelfActionDialog(
            shelf = editShelfClicked,
            onAction = { type, shelf ->
                coroutine.launch {
                    when(type) {
                        ShelfActionType.ADD_UPDATE -> { shelfViewModel.update(shelf) }
                        ShelfActionType.DELETE -> {
                            if(!shelfViewModel.delete(shelf)){
                                SnackbarManager.send("Some bottles are stored in this shelf, cannot delete it")
                            }
                        }
                        else -> {}
                    }
                }
            },
            onDismiss = { showEditDialog = false }
        )
    }

    positionClicked?.let { position ->
        OnBottlePositionClick(
            wineViewModel = wineViewModel,
            stockViewModel = stockViewModel,
            position = position,
            onPlaceWine = {position, wine, comment ->
                coroutine.launch { stockViewModel.insert(position, wine, comment) }
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