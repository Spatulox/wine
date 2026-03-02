package com.spatulox.wine.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun MainMenu(
    wineViewModel: WineViewModel,
    historyViewModel: HistoryViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Overview", "History")

    Scaffold(
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

            // Contenu qui change selon le tab sélectionné
            when (selectedTabIndex) {
                0 -> OverviewScreen(wineViewModel = wineViewModel)
                1 -> HistoryScreen(historyViewModel = historyViewModel)
                else -> Text("Écran non implémenté")
            }
        }
    }
}
