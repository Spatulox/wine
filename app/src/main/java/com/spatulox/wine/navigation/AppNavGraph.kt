package com.spatulox.wine.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spatulox.wine.ui.screens.history.HistoryScreen
import com.spatulox.wine.ui.screens.MainMenu
import com.spatulox.wine.ui.screens.overview.OverviewScreen
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun AppNavGraph(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    historyViewModel: HistoryViewModel
) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Destinations.MAIN_MENU,
    ) {

        composable(Destinations.MAIN_MENU) {
            MainMenu(
                wineViewModel = wineViewModel,
                stockViewModel = stockViewModel,
                historyViewModel = historyViewModel
            )
        }

        composable(Destinations.OVERVIEW) {
            OverviewScreen(
                stockViewModel = stockViewModel,
                wineViewModel = wineViewModel
            )
        }
        composable(Destinations.HISTORY) {
            HistoryScreen(historyViewModel = historyViewModel)
        }
    }
}
