package com.spatulox.wine.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import com.spatulox.wine.SnackbarManager
import com.spatulox.wine.ui.screens.components.AddButton
import com.spatulox.wine.ui.screens.components.SearchWithFilters
import com.spatulox.wine.ui.screens.shelf.ShelfScreen
import com.spatulox.wine.ui.screens.wine.WineScreen
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun MainMenu(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    shelfViewModel: ShelfViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddWineDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Cave", "Wines", "Statistics")

    var isFabExpanded by remember { mutableStateOf(false) }


    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.INSTANCE.messageFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (selectedTabIndex == 1) {
                    AddButton (
                        onClick = { showAddWineDialog = true },
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }

                SearchWithFilters(
                    wineViewModel = wineViewModel,
                    stockViewModel = stockViewModel,
                    isExpanded = isFabExpanded,
                    onExpandedChange = { isFabExpanded = it },
                    modifier = Modifier.align(Alignment.BottomEnd),
                    selectedTabIndex = selectedTabIndex
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (selectedTabIndex == index)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(isFabExpanded) {
                        if (isFabExpanded) {
                            detectTapGestures(onTap = {
                                isFabExpanded = false
                                wineViewModel.clearFilter()
                                stockViewModel.clearFilter()
                            })
                        }
                    }
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        ShelfScreen(
                            stockViewModel = stockViewModel,
                            wineViewModel = wineViewModel,
                            shelfViewModel = shelfViewModel
                        )
                    }
                    1 -> {
                        WineScreen(
                            wineViewModel = wineViewModel,
                            stockViewModel = stockViewModel,
                            showAddDialog = showAddWineDialog,
                            onAddDialogChange = { showAddWineDialog = it },
                            onChangeTabScreen = { filter ->
                                selectedTabIndex = 0
                                isFabExpanded = true
                                stockViewModel.updateFilter(filter)
                                wineViewModel.updateFilter(filter)
                            }
                        )
                    }
                    else -> Text("Écran non implémenté")
                }
            }
        }
    }
}
