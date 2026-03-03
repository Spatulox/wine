package com.spatulox.wine.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import com.spatulox.wine.ui.screens.components.AddButton
import com.spatulox.wine.ui.screens.components.SearchWithFilters
import com.spatulox.wine.ui.screens.history.HistoryScreen
import com.spatulox.wine.ui.screens.shelf.ShelfScreen
import com.spatulox.wine.ui.screens.wine.WineScreen
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun MainMenu(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    historyViewModel: HistoryViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddWineDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Shelves", "Wines", "History")



    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                // AddButton À GAUCHE (Start)
                if (selectedTabIndex == 1) {
                    AddButton (
                        onClick = { showAddWineDialog = true },
                        modifier = Modifier.align(Alignment.BottomStart)//.padding(start = 16.dp)
                    )
                }

                // SearchWithFilters À DROITE (End)
                SearchWithFilters(
                    wineViewModel = wineViewModel,
                    stockViewModel = stockViewModel,
                    historyViewModel = historyViewModel,
                    isExpanded = isFabExpanded,
                    onExpandedChange = { isFabExpanded = it },
                    onOutsideClick = { isFabExpanded = false },
                    modifier = Modifier.align(Alignment.BottomEnd)
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
                            detectTapGestures(onTap = { isFabExpanded = false })
                        }
                    }
            ) {
                when (selectedTabIndex) {
                    0 -> ShelfScreen(
                        stockViewModel = stockViewModel,
                        wineViewModel = wineViewModel
                    )
                    1 -> WineScreen(
                        wineViewModel = wineViewModel,
                        showAddDialog = showAddWineDialog,
                        onAddDialogChange = { showAddWineDialog = it }
                    )
                    2 -> HistoryScreen(historyViewModel = historyViewModel)
                    else -> Text("Écran non implémenté")
                }
            }
        }
    }
}
