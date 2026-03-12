package com.spatulox.wine.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spatulox.wine.ui.screens.MainMenu
import com.spatulox.wine.ui.screens.shelf.CompartmentActionDialog
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun AppNavGraph(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    shelfViewModel: ShelfViewModel,
    compartmentViewModel: CompartmentViewModel
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
                shelfViewModel = shelfViewModel,
                compartmentViewModel = compartmentViewModel,
                navController = nav,
            )
        }

        composable (Destinations.COMPARTMENT_ADD) {
            CompartmentActionDialog(
                navController = nav,
                compartmentViewModel = compartmentViewModel,
                shelfViewModel = shelfViewModel
            )
        }

        composable("${Destinations.COMPARTMENT_EDIT}/{compartmentId}") { backStackEntry ->
            val compartmentId = backStackEntry.arguments?.getString("compartmentId")

            CompartmentActionDialog(
                navController = nav,
                compartmentViewModel = compartmentViewModel,
                shelfViewModel = shelfViewModel,
                compartmentId = compartmentId
            )
        }

    }
}
