package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.navigation.Destinations
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.launch

@Composable
fun CompartmentScreen(
    stockViewModel: StockViewModel,
    wineViewModel: WineViewModel,
    shelfViewModel: ShelfViewModel,
    compartmentViewModel: CompartmentViewModel,
    modifier: Modifier = Modifier,
    onPositionClick: (position: Position) -> Unit = { _ -> },
    navController: NavController,
) {
    val stockState by stockViewModel.stockState.collectAsStateWithLifecycle()
    val winesPositionMap by wineViewModel.filteredWinesMap.collectAsStateWithLifecycle()

    val compartment by compartmentViewModel.compartments.collectAsStateWithLifecycle()
    val shelvesByCompartment by shelfViewModel.shelvesByCompartmentId.collectAsStateWithLifecycle()

    val coroutine = rememberCoroutineScope()

    var positionClicked by remember { mutableStateOf<Position?>(null) }

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



    Column(modifier = modifier.fillMaxSize()) {
        if (errorMessage.isNotBlank()) {
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
                CompartmentView(
                    compartment = compartment[index],
                    shelves = shelvesByCompartment[compartment[index].id],
                    stock = stockState,
                    wines = winesPositionMap,
                    onPositionClick = { position -> positionClicked = position },
                    onEditClick = {
                        //navController.navigate(Destinations.COMPARTMENT_EDIT)
                        navController.navigate("${Destinations.COMPARTMENT_EDIT}/${compartment[index].id}")
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

    positionClicked?.let { position ->
        OnBottlePositionClick(
            wineViewModel = wineViewModel,
            stockViewModel = stockViewModel,
            position = position,
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