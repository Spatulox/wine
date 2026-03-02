package com.spatulox.wine.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spatulox.wine.ui.screens.HistoryScreen
import com.spatulox.wine.ui.screens.MainMenu
import com.spatulox.wine.ui.screens.OverviewScreen
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.WineViewModel

@Composable
fun AppNavGraph(
    wineViewModel: WineViewModel,
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
                historyViewModel = historyViewModel
            )
        }

        composable(Destinations.OVERVIEW) {
            OverviewScreen(wineViewModel = wineViewModel)
        }
        composable(Destinations.HISTORY) {
            HistoryScreen(historyViewModel = historyViewModel)
        }
    }
}
