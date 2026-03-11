package com.spatulox.wine.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spatulox.wine.ui.screens.MainMenu
import com.spatulox.wine.ui.screens.shelf.ShelfScreen
import com.spatulox.wine.ui.screens.wine.WineScreen
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun AppNavGraph(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    shelfViewModel: ShelfViewModel
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
                shelfViewModel = shelfViewModel
            )
        }
    }
}
